package com.cinema.booking.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class Hall {
    private final Long id;
    private final String name;
    private final int rowsCount;
    private final int seatsPerRow;
    private final List<Seat> seats;

    public Hall(Long id, String name, int rowsCount, int seatsPerRow) {
        if (rowsCount <= 0 || seatsPerRow <= 0) {
            throw new IllegalArgumentException("Количество рядов и мест в ряду должно быть больше нуля.");
        }
        this.id = Objects.requireNonNull(id, "ID зала не может быть null.");
        this.name = Objects.requireNonNull(name, "Название зала не может быть null.");
        this.rowsCount = rowsCount;
        this.seatsPerRow = seatsPerRow;
        this.seats = generateSeatsGrid(rowsCount, seatsPerRow);
    }

    /**
     * Генерация сетки мест. Последние ряды (например, 20% от зала) помечаются как VIP.
     */
    private List<Seat> generateSeatsGrid(int rows, int perRow) {
        List<Seat> generatedSeats = new ArrayList<>(rows * perRow);
        int vipStartRow = Math.max(1, rows - (rows > 3 ? 1 : 0)); // Последний ряд делаем VIP

        for (int r = 1; r <= rows; r++) {
            SeatType type = (r >= vipStartRow) ? SeatType.VIP : SeatType.STANDARD;
            for (int s = 1; s <= perRow; s++) {
                generatedSeats.add(new Seat(r, s, type));
            }
        }
        return Collections.unmodifiableList(generatedSeats); // Защитное копирование: зал неизменен
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRowsCount() {
        return rowsCount;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public int getTotalSeatsCount() {
        return rowsCount * seatsPerRow;
    }

    public List<Seat> getSeats() {
        return seats; // Уже обёрнут в unmodifiableList
    }

    public Optional<Seat> findSeat(int row, int seatNumber) {
        return seats.stream()
                .filter(s -> s.getRowNumber() == row && s.getSeatNumber() == seatNumber)
                .findFirst();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hall hall = (Hall) o;
        return Objects.equals(id, hall.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Зал '%s' [ID: %d] (%d рядов × %d мест, всего: %d)", 
                name, id, rowsCount, seatsPerRow, getTotalSeatsCount());
    }
}
