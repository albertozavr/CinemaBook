package com.cinema.booking.exception;

/**
 * Выбрасывается, если запрашиваемое место с указанным рядом и номером отсутствует в зале.
 */
public class SeatNotFoundException extends CinemaException {
    public SeatNotFoundException(int rowNumber, int seatNumber, String hallName) {
        super(String.format("Место (Ряд %d, Место %d) не существует в зале '%s'.", 
                rowNumber, seatNumber, hallName));
    }
}
