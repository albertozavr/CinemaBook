package com.cinema.booking.model;

public enum BookingStatus {
    ACTIVE("Активна"),
    CANCELLED("Отменена");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
