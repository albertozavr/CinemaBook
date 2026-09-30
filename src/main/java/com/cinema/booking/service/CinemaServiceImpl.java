package com.cinema.booking.service;

import com.cinema.booking.exception.*;
import com.cinema.booking.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;


public class CinemaServiceImpl implements CinemaService {

    // Хранилища сущностей в памяти (In-Memory Data Store)
    private final Map<Long, User> users = new HashMap<>();
    private final Map<Long, Hall> halls = new HashMap<>();
    private final Map<Long, MovieSession> sessions = new HashMap<>();
    private final Map<Long, Booking> bookings = new HashMap<>();
    private final List<Booking> bookingHistory = new ArrayList<>();

    // Генераторы ID
    private final AtomicLong userIdSeq = new AtomicLong(1);
    private final AtomicLong hallIdSeq = new AtomicLong(1);
    private final AtomicLong sessionIdSeq = new AtomicLong(1);
    private final AtomicLong bookingIdSeq = new AtomicLong(1);

    @Override
    public Hall createHall(String name, int rows, int seatsPerRow) {
        Long id = hallIdSeq.getAndIncrement();
        Hall hall = new Hall(id, name, rows, seatsPerRow);
        halls.put(id, hall);
        return hall;
    }

    @Override
    public MovieSession createSession(String movieTitle, Long hallId, LocalDateTime startTime) {
        Hall hall = halls.get(hallId);
        if (hall == null) {
            throw new IllegalArgumentException("Зал с ID " + hallId + " не найден.");
        }
        Long id = sessionIdSeq.getAndIncrement();
        MovieSession session = new MovieSession(id, movieTitle, hall, startTime);
        sessions.put(id, session);
        return session;
    }

    @Override
    public User registerUser(String fullName, String email, String phone) {
        Long id = userIdSeq.getAndIncrement();
        User user = new User(id, fullName, email, phone);
        users.put(id, user);
        return user;
    }

    @Override
    public List<Seat> getAvailableSeats(Long sessionId) {
        MovieSession session = getSessionOrThrow(sessionId);
        Hall hall = session.getHall();
        Set<Seat> bookedSeats = session.getBookedSeats();

        // Фильтруем все места зала, оставляя те, которых нет в множестве bookedSeats
        return hall.getSeats().stream()
                .filter(seat -> !bookedSeats.contains(seat))
                .collect(Collectors.toList());
    }

    @Override
    public Booking bookSeat(Long sessionId, Long userId, int rowNumber, int seatNumber) {
        MovieSession session = getSessionOrThrow(sessionId);
        User user = getUserOrThrow(userId);

        // 1. Ищем место в зале по ряду и номеру
        Seat seat = session.getHall().findSeat(rowNumber, seatNumber)
                .orElseThrow(() -> new SeatNotFoundException(rowNumber, seatNumber, session.getHall().getName()));

        // 2. Атомарная проверка и резервация в Set<Seat>
        // Метод reserveSeat() использует Set.add(seat): возвращает false, если место уже есть в множестве
        boolean reservedSuccessfully = session.reserveSeat(seat);
        if (!reservedSuccessfully) {
            throw new SeatAlreadyBookedException(sessionId, rowNumber, seatNumber);
        }

        // 3. Создаем бронь и сохраняем в истории
        Long bookingId = bookingIdSeq.getAndIncrement();
        BigDecimal price = seat.getType().getBasePrice();
        Booking booking = new Booking(bookingId, user, session, seat, price);

        bookings.put(bookingId, booking);
        bookingHistory.add(booking);

        return booking;
    }

    @Override
    public Booking cancelBooking(Long bookingId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) {
            throw new BookingNotFoundException(bookingId);
        }

        if (!booking.isActive()) {
            throw new IllegalStateException("Бронирование #" + bookingId + " уже было отменено ранее.");
        }

        // 1. Помечаем статус бронирования как CANCELLED
        booking.cancel();

        // 2. Освобождаем место в сеансе (удаляем из Set<Seat>)
        booking.getSession().releaseSeat(booking.getSeat());

        return booking;
    }

    @Override
    public List<Booking> getUserBookings(Long userId) {
        getUserOrThrow(userId); // проверка существования пользователя

        return bookingHistory.stream()
                .filter(b -> b.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public SessionStats getSessionStats(Long sessionId) {
        MovieSession session = getSessionOrThrow(sessionId);
        Hall hall = session.getHall();

        int totalSeats = hall.getTotalSeatsCount();
        int bookedCount = session.getBookedSeats().size();
        int freeCount = totalSeats - bookedCount;

        double occupancyPercent = totalSeats > 0 
                ? ((double) bookedCount / totalSeats) * 100.0 
                : 0.0;

        // Выручка суммируется только по активным бронированиям данного сеанса
        BigDecimal totalRevenue = bookingHistory.stream()
                .filter(b -> b.getSession().getId().equals(sessionId) && b.isActive())
                .map(Booking::getPricePaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new SessionStats(
                session.getId(),
                session.getMovieTitle(),
                totalSeats,
                bookedCount,
                freeCount,
                occupancyPercent,
                totalRevenue
        );
    }

    @Override
    public MovieSession getSessionById(Long sessionId) {
        return getSessionOrThrow(sessionId);
    }

    @Override
    public List<MovieSession> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    @Override
    public List<Hall> getAllHalls() {
        return new ArrayList<>(halls.values());
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    // Вспомогательные приватные методы валидации
    private MovieSession getSessionOrThrow(Long sessionId) {
        MovieSession session = sessions.get(sessionId);
        if (session == null) {
            throw new SessionNotFoundException(sessionId);
        }
        return session;
    }

    private User getUserOrThrow(Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user;
    }
}
