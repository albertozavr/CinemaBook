package com.cinema.booking.exception;

/**
 * Выбрасывается при попытке забронировать место, которое уже занято на данный сеанс.
 * Защищает от состояния двойного бронирования (Double Booking).
 */
public class SeatAlreadyBookedException extends CinemaException {
    private final Long sessionId;
    private final int rowNumber;
    private final int seatNumber;

    public SeatAlreadyBookedException(Long sessionId, int rowNumber, int seatNumber) {
        super(String.format("Место (Ряд %d, Место %d) на сеанс ID %d уже забронировано!", 
                rowNumber, seatNumber, sessionId));
        this.sessionId = sessionId;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }
}
