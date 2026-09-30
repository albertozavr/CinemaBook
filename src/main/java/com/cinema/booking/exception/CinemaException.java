package com.cinema.booking.exception;

/**
 * Базовое непроверяемое (unchecked) исключение предметной области кинотеатра.
 * 
 * АРХИТЕКТУРНОЕ ОБОСНОВАНИЕ:
 * В современном Java (начиная с Java 8 и особенно в Spring/Enterprise экосистемах)
 * принято наследовать бизнес-исключения от RuntimeException. Это предотвращает
 * загрязнение сигнатур методов (throws) и избыточный boilerplate-код, при этом
 * позволяя централизованно перехватывать ошибки на верхнем уровне приложения.
 */
public class CinemaException extends RuntimeException {
    public CinemaException(String message) {
        super(message);
    }

    public CinemaException(String message, Throwable cause) {
        super(message, cause);
    }
}
