package com.cinema.booking.exception;

/**
 * Выбрасывается, когда сеанс с заданным ID не найден в системе.
 */
public class SessionNotFoundException extends CinemaException {
    public SessionNotFoundException(Long sessionId) {
        super(String.format("Сеанс с ID %d не найден в системе.", sessionId));
    }
}
