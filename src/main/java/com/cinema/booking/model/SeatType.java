package com.cinema.booking.model;

import java.math.BigDecimal;

public enum SeatType {
    STANDARD(new BigDecimal("350.00")),
    VIP(new BigDecimal("700.00"));

    private final BigDecimal basePrice;

    SeatType(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    @Override
    public String toString() {
        return name() + " (" + basePrice + " ₽)";
    }
}
