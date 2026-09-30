package com.cinema.booking.model;

import java.math.BigDecimal;

public record SessionStats(
        Long sessionId,
        String movieTitle,
        int totalSeats,
        int bookedSeatsCount,
        int freeSeatsCount,
        double occupancyPercentage,
        BigDecimal totalRevenue
) {
    @Override
    public String toString() {
        return String.format("""
                === Статистика сеанса #%d: '%s' ===
                - Всего мест:        %d
                - Занято мест:       %d
                - Свободно мест:     %d
                - Заполняемость:     %.2f%%
                - Общая выручка:     %s ₽
                """,
                sessionId, movieTitle, totalSeats, bookedSeatsCount, freeSeatsCount,
                occupancyPercentage, totalRevenue);
    }
}
