package com.cinema.booking.service;

import com.cinema.booking.exception.*;
import com.cinema.booking.model.*;

import java.time.LocalDateTime;
import java.util.List;

public interface CinemaService {

    Hall createHall(String name, int rows, int seatsPerRow);

    MovieSession createSession(String movieTitle, Long hallId, LocalDateTime startTime);

    User registerUser(String fullName, String email, String phone);


    List<Seat> getAvailableSeats(Long sessionId);


    Booking bookSeat(Long sessionId, Long userId, int rowNumber, int seatNumber);


    Booking cancelBooking(Long bookingId);

    List<Booking> getUserBookings(Long userId);

    SessionStats getSessionStats(Long sessionId);

    MovieSession getSessionById(Long sessionId);

    List<MovieSession> getAllSessions();

    List<Hall> getAllHalls();

    List<User> getAllUsers();
}
