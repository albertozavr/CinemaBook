package com.cinema.booking.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


public class MovieSession {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final Long id;
    private final String movieTitle;
    private final Hall hall;
    private final LocalDateTime startTime;
    private final Set<Seat> bookedSeats;

    public MovieSession(Long id, String movieTitle, Hall hall, LocalDateTime startTime) {
        this.id = Objects.requireNonNull(id, "ID сеанса не может быть null.");
        this.movieTitle = Objects.requireNonNull(movieTitle, "Название фильма не может быть null.");
        this.hall = Objects.requireNonNull(hall, "Зал не может быть null.");
        this.startTime = Objects.requireNonNull(startTime, "Время сеанса не может быть null.");
        this.bookedSeats = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public Hall getHall() {
        return hall;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Возвращает неизменяемое представление множества занятых мест.
     * Защищает внутреннее состояние от несанкционированной модификации извне.
     */
    public Set<Seat> getBookedSeats() {
        return Collections.unmodifiableSet(bookedSeats);
    }

    /**
     * Проверка занятости места за O(1).
     */
    public boolean isSeatBooked(Seat seat) {
        return bookedSeats.contains(seat);
    }

    /**
     * Попытка занять место.
     * @return true, если место успешно добавлено в Set; false, если уже было занято.
     */
    public boolean reserveSeat(Seat seat) {
        return bookedSeats.add(seat);
    }

    /**
     * Освобождение места при отмене брони.
     * @return true, если место было удалено из Set.
     */
    public boolean releaseSeat(Seat seat) {
        return bookedSeats.remove(seat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MovieSession that = (MovieSession) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Сеанс #%d: '%s' | Зал: '%s' | Начало: %s",
                id, movieTitle, hall.getName(), startTime.format(FORMATTER));
    }
}
