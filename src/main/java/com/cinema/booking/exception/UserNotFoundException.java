package com.cinema.booking.exception;

/**
 * Выбрасывается, когда пользователь с заданным ID не найден в системе.
 */
public class UserNotFoundException extends CinemaException {
    public UserNotFoundException(Long userId) {
        super(String.format("Пользователь с ID %d не найден в системе.", userId));
    }
}
