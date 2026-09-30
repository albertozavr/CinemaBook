package com.cinema.booking.model;

import java.util.Objects;


public class Seat {
    private final int rowNumber;
    private final int seatNumber;
    private final SeatType type;

    public Seat(int rowNumber, int seatNumber, SeatType type) {
        if (rowNumber <= 0 || seatNumber <= 0) {
            throw new IllegalArgumentException("Номер ряда и места должны быть положительными числами.");
        }
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.type = Objects.requireNonNull(type, "Тип места не может быть null.");
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public SeatType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return rowNumber == seat.rowNumber && seatNumber == seat.seatNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowNumber, seatNumber);
    }

    @Override
    public String toString() {
        return String.format("Ряд %d, Место %d [%s]", rowNumber, seatNumber, type);
    }
}
