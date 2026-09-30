# Cinema Booking System

 «Система бронирования мест в кинотеатре», реализованный на чистой Java

## Цели проекта и проверяемые навыки
1. **ООП и Чистая Архитектура**:
   - Четкое разделение слоев: `model` (сущности), `exception` (доменные исключения), `service` (бизнес-логика).
   - Инкапсуляция внутреннего состояния (например, `Collections.unmodifiableList`, `unmodifiableSet`).
   - Использование интерфейса `CinemaService` и его конкретной реализации `CinemaServiceImpl`.
2. **Collections Framework**:
   - `Map<Long, T>` (`HashMap`) для быстрого $O(1)$ доступа к пользователям, залам, сеансам и броням.
   - `Set<Seat>` (`HashSet`) для мгновенной проверки занятости мест и предотвращения повторного бронирования.
   - `List<Booking>` для ведения журнала бронирований.
3. **Контракт `equals()` и `hashCode()`**:
   - В классе `Seat` переопределен контракт по `(rowNumber, seatNumber)`. Это гарантирует, что `Set<Seat>.contains()` и `add()` работают корректно независимо от ссылки объекта.
4. **Обработка исключений**:
   - Иерархия бизнес-исключений с базовым `CinemaException extends RuntimeException`.
   - Защита от двойного бронирования (`SeatAlreadyBookedException`).
   - `SeatNotFoundException`, `BookingNotFoundException`, `SessionNotFoundException`, `UserNotFoundException`.
5. **Современный Java 17+**:
   - Java Records (`record SessionStats`).
   - Text Blocks (`"""`).
   - Stream API & `Optional`.
   - `BigDecimal` для денежных расчетов.

---


## Запуск проекта

```bash
# Запуск через Maven:
mvn compile exec:java

