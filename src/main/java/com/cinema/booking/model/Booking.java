package com.cinema.booking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Booking {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final Long id;
    private final User user;
    private final MovieSession session;
    private final Seat seat;
    private final LocalDateTime bookedAt;
    private final BigDecimal pricePaid;
    private BookingStatus status;

    public Booking(Long id, User user, MovieSession session, Seat seat, BigDecimal pricePaid) {
        this.id = Objects.requireNonNull(id, "ID брони не может быть null.");
        this.user = Objects.requireNonNull(user, "Пользователь не может быть null.");
        this.session = Objects.requireNonNull(session, "Сеанс не может быть null.");
        this.seat = Objects.requireNonNull(seat, "Место не может быть null.");
        this.pricePaid = Objects.requireNonNull(pricePaid, "Стоимость не может быть null.");
        this.bookedAt = LocalDateTime.now();
        this.status = BookingStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public MovieSession getSession() {
        return session;
    }

    public Seat getSeat() {
        return seat;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public BigDecimal getPricePaid() {
        return pricePaid;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    public boolean isActive() {
        return this.status == BookingStatus.ACTIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Бронь #%d [%s] | Фильм: '%s' | %s | Клиент: %s | Стоимость: %s ₽ | Создана: %s",
                id, status.getDescription(), session.getMovieTitle(), seat, user.getFullName(),
                pricePaid, bookedAt.format(FORMATTER));
    }
}
