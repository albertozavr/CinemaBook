package com.cinema.booking.exception;

/**
 * Выбрасывается, если бронирование с указанным идентификатором не найдено.
 */
public class BookingNotFoundException extends CinemaException {
    public BookingNotFoundException(Long bookingId) {
        super(String.format("Бронирование с ID %d не найдено.", bookingId));
    }
}
