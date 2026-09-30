package com.cinema.booking;

import com.cinema.booking.exception.CinemaException;
import com.cinema.booking.exception.SeatAlreadyBookedException;
import com.cinema.booking.model.*;
import com.cinema.booking.service.CinemaService;
import com.cinema.booking.service.CinemaServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


public class Main {

    private final CinemaService cinemaService;
    private final Scanner scanner;
    private User currentUser;

    public Main(CinemaService cinemaService, Scanner scanner) {
        this.cinemaService = cinemaService;
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        CinemaService service = new CinemaServiceImpl();
        initDefaultData(service);

        Scanner scanner = new Scanner(System.in);
        Main app = new Main(service, scanner);
        app.run();
    }

    private static void initDefaultData(CinemaService service) {
        // Создаем залы
        Hall hall1 = service.createHall("Красный зал (IMAX 4K)", 4, 6);
        Hall hall2 = service.createHall("Синий зал (Dolby Atmos)", 3, 5);

        // Создаем базовые сеансы
        service.createSession("Интерстеллар (Перевыпуск)", hall1.getId(), LocalDateTime.now().plusHours(2));
        service.createSession("Оппенгеймер", hall2.getId(), LocalDateTime.now().plusHours(4));
        service.createSession("Дюна: Часть вторая", hall1.getId(), LocalDateTime.now().plusDays(1).withHour(19).withMinute(0));

        // Создаем начальных пользователей
        User u1 = service.registerUser("Алексей Смирнов", "alexey@example.com", "+7 (999) 111-22-33");
        User u2 = service.registerUser("Елена Кузнецова", "elena@example.com", "+7 (999) 444-55-66");

        // Сделаем пару предварительных броней для наглядности
        service.bookSeat(1L, u1.getId(), 2, 3);
        service.bookSeat(1L, u2.getId(), 4, 4); // VIP
    }

    public void run() {
        System.out.println("=====================================================================");
        System.out.println("   ДОБРО ПОЖАЛОВАТЬ В КИНОТЕАТР: ИНТЕРАКТИВНЫЙ РЕЖИМ БРОНИРОВАНИЯ   ");
        System.out.println("=====================================================================");

        // Автоматически авторизуем первого пользователя или создаем нового
        List<User> users = cinemaService.getAllUsers();
        if (!users.isEmpty()) {
            currentUser = users.get(0);
        } else {
            currentUser = cinemaService.registerUser("Гость", "guest@example.com", "+7 000 000-00-00");
        }

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> showAllSessions();
                case "2" -> showHallPlanForSession();
                case "3" -> handleBooking();
                case "4" -> handleCancellation();
                case "5" -> showUserHistory();
                case "6" -> showSessionStats();
                case "7" -> switchOrRegisterUser();
                case "8" -> addNewSession();
                case "0" -> {
                    System.out.println("\nСпасибо за использование системы бронирования! До свидания!");
                    running = false;
                }
                default -> System.out.println("Неверный выбор! Введите число от 0 до 8.");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n---------------------------------------------------------------------");
        System.out.printf(" Текущий пользователь: \u001B[33m%s\u001B[0m (ID: %d, %s)%n",
                currentUser.getFullName(), currentUser.getId(), currentUser.getEmail());
        System.out.println("---------------------------------------------------------------------");
        System.out.println(" [1] Список доступных сеансов");
        System.out.println(" [2] Показать схему зала для сеанса (свободные/занятые места)");
        System.out.println(" [3] Забронировать место (Выбор ряда и места)");
        System.out.println(" [4] Отменить существующую бронь");
        System.out.println(" [5] Мои бронирования (История пользователя)");
        System.out.println(" [6] Статистика и выручка сеанса");
        System.out.println(" [7] Сменить или зарегистрировать нового пользователя");
        System.out.println(" [8] Создать новый сеанс");
        System.out.println(" [0] Выход из программы");
        System.out.print(" Выберите действие (0-8): ");
    }

    private void showAllSessions() {
        System.out.println("\n=== РАСПИСАНИЕ СЕАНСОВ ===");
        List<MovieSession> sessions = cinemaService.getAllSessions();
        if (sessions.isEmpty()) {
            System.out.println("Сеансов пока нет.");
            return;
        }

        for (MovieSession s : sessions) {
            int total = s.getHall().getTotalSeatsCount();
            int booked = s.getBookedSeats().size();
            int free = total - booked;
            System.out.printf("  [%d] %-30s | Зал: %-25s | Свободно: %d из %d%n",
                    s.getId(), s.getMovieTitle(), s.getHall().getName(), free, total);
        }
    }

    private void showHallPlanForSession() {
        showAllSessions();
        System.out.print("\nВведите ID сеанса для просмотра карты зала: ");
        Long sessionId = parseLongInput();
        if (sessionId == null) return;

        try {
            printHallPlan(sessionId);
        } catch (CinemaException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void printHallPlan(Long sessionId) {
        MovieSession session = cinemaService.getSessionById(sessionId);
        Hall hall = session.getHall();
        Set<Seat> bookedSeats = session.getBookedSeats();

        System.out.println("\n=======================================================");
        System.out.printf("  СХЕМА ЗАЛА '%s' ДЛЯ СЕАНСА '%s'%n", hall.getName(), session.getMovieTitle());
        System.out.println("=======================================================");
        System.out.println("                  Э   К   Р   А   Н                    ");
        System.out.println("-------------------------------------------------------");

        for (int r = 1; r <= hall.getRowsCount(); r++) {
            System.out.printf("Ряд %-2d | ", r);
            for (int s = 1; s <= hall.getSeatsPerRow(); s++) {
                final int row = r;
                final int seatNum = s;
                Seat seat = hall.findSeat(row, seatNum).orElseThrow();

                if (bookedSeats.contains(seat)) {
                    System.out.print("[ X ] "); // Занято
                } else if (seat.getType() == SeatType.VIP) {
                    System.out.print("[ V ] "); // VIP
                } else {
                    System.out.print("[ S ] "); // Standard
                }
            }
            System.out.println();
        }
        System.out.println("-------------------------------------------------------");
        System.out.println("Обозначения: [ S ] - Standard (350 ₽) | [ V ] - VIP (700 ₽) | [ X ] - ЗАНЯТО\n");
    }

    private void handleBooking() {
        showAllSessions();
        System.out.print("\nВведите ID сеанса: ");
        Long sessionId = parseLongInput();
        if (sessionId == null) return;

        try {
            printHallPlan(sessionId);

            System.out.print("Введите номер ряда: ");
            Integer row = parseIntegerInput();
            if (row == null) return;

            System.out.print("Введите номер места: ");
            Integer seat = parseIntegerInput();
            if (seat == null) return;

            System.out.printf("Выполняется бронирование: Ряд %d, Место %d для %s...%n",
                    row, seat, currentUser.getFullName());

            Booking booking = cinemaService.bookSeat(sessionId, currentUser.getId(), row, seat);
            System.out.println("\nУСПЕШНО ЗАРЕГИСТРИРОВАНА БРОНЬ!");
            System.out.printf("   Номер билета: #%d%n", booking.getId());
            System.out.printf("   Фильм:        %s%n", booking.getSession().getMovieTitle());
            System.out.printf("   Место:        Ряд %d, Место %d [%s]%n", booking.getSeat().getRowNumber(), booking.getSeat().getSeatNumber(), booking.getSeat().getType());
            System.out.printf("   К оплате:     %s ₽%n", booking.getPricePaid());
            System.out.printf("   Статус:       %s%n", booking.getStatus().getDescription());

        } catch (SeatAlreadyBookedException e) {
            System.out.println("\n[ОШИБКА ДВОЙНОГО БРОНИРОВАНИЯ - SeatAlreadyBookedException]");
            System.out.println("   " + e.getMessage());
        } catch (CinemaException e) {
            System.out.println("\nОшибка: " + e.getMessage());
        }
    }

    private void handleCancellation() {
        showUserHistory();
        System.out.print("Введите ID бронирования для отмены: ");
        Long bookingId = parseLongInput();
        if (bookingId == null) return;

        try {
            Booking cancelled = cinemaService.cancelBooking(bookingId);
            System.out.printf("Бронирование #%d успешно отменено! Место (Ряд %d, Место %d) теперь свободно.%n",
                    cancelled.getId(), cancelled.getSeat().getRowNumber(), cancelled.getSeat().getSeatNumber());
        } catch (CinemaException e) {
            System.out.println("Ошибка отмены: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
        }
    }

    private void showUserHistory() {
        System.out.printf("%n=== ИСТОРИЯ БРОНИРОВАНИЙ ПОЛЬЗОВАТЕЛЯ '%s' ===%n", currentUser.getFullName());
        List<Booking> bookings = cinemaService.getUserBookings(currentUser.getId());
        if (bookings.isEmpty()) {
            System.out.println("У вас пока нет оформленных бронирований.");
            return;
        }

        for (Booking b : bookings) {
            String statusColor = b.isActive() ? "\u001B[32m" : "\u001B[31m";
            System.out.printf("  • Бронь #%d | Статус: %s%-9s\u001B[0m | Сеанс: %-25s | %s | %s ₽ | %s%n",
                    b.getId(),
                    statusColor,
                    b.getStatus().getDescription(),
                    b.getSession().getMovieTitle(),
                    b.getSeat(),
                    b.getPricePaid(),
                    b.getBookedAt());
        }
        System.out.println();
    }

    private void showSessionStats() {
        showAllSessions();
        System.out.print("\nВведите ID сеанса для просмотра статистики: ");
        Long sessionId = parseLongInput();
        if (sessionId == null) return;

        try {
            SessionStats stats = cinemaService.getSessionStats(sessionId);
            System.out.println("\n" + stats);
        } catch (CinemaException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
    private void switchOrRegisterUser() {
        System.out.println("\n=== УПРАВЛЕНИЕ ПОЛЬЗОВАТЕЛЕМ ===");
        System.out.println("Список существующих пользователей:");
        List<User> allUsers = cinemaService.getAllUsers();
        for (User u : allUsers) {
            System.out.printf("  [%d] %-20s (email: %s)%n", u.getId(), u.getFullName(), u.getEmail());
        }

        System.out.println("\n[1] Выбрать существующего пользователя по ID");
        System.out.println("[2] Зарегистрировать нового пользователя");
        System.out.print("Выберите (1 или 2): ");
        String choice = scanner.nextLine().trim();

        if ("1".equals(choice)) {
            System.out.print("Введите ID пользователя: ");
            Long id = parseLongInput();
            if (id != null) {
                User found = allUsers.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
                if (found != null) {
                    currentUser = found;
                    System.out.println("Переключено на пользователя: " + currentUser.getFullName());
                } else {
                    System.out.println("Пользователь с таким ID не найден.");
                }
            }
        } else if ("2".equals(choice)) {
            System.out.print("Введите ФИО пользователя: ");
            String name = scanner.nextLine().trim();
            System.out.print("Введите Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Введите телефон: ");
            String phone = scanner.nextLine().trim();

            if (name.isEmpty() || email.isEmpty()) {
                System.out.println("Имя и Email не могут быть пустыми.");
                return;
            }

            User created = cinemaService.registerUser(name, email, phone);
            currentUser = created;
            System.out.println("Пользователь успешно создан и авторизован: " + created.getFullName());
        }
    }

    private void addNewSession() {
        System.out.println("\n=== СОЗДАНИЕ НОВОГО СЕАНСА ===");
        System.out.print("Введите название фильма: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Название не может быть пустым.");
            return;
        }

        System.out.println("Доступные залы:");
        List<Hall> halls = cinemaService.getAllHalls();
        for (Hall h : halls) {
            System.out.printf("  [%d] %s%n", h.getId(), h.getName());
        }
        System.out.print("Введите ID зала: ");
        Long hallId = parseLongInput();
        if (hallId == null) return;

        try {
            MovieSession session = cinemaService.createSession(title, hallId, LocalDateTime.now().plusHours(3));
            System.out.printf("Сеанс #%d на фильм '%s' успешно создан!%n", session.getId(), session.getMovieTitle());
        } catch (Exception e) {
            System.out.println("Ошибка при создании сеанса: " + e.getMessage());
        }
    }

    private Long parseLongInput() {
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите корректное число.");
            return null;
        }
    }

    private Integer parseIntegerInput() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка: введите корректное число.");
            return null;
        }
    }
}
