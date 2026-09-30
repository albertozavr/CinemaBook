package com.cinema.booking.model;

import java.util.Objects;

public class User {
    private final Long id;
    private final String fullName;
    private final String email;
    private final String phone;

    public User(Long id, String fullName, String email, String phone) {
        this.id = Objects.requireNonNull(id, "ID пользователя не может быть null.");
        this.fullName = Objects.requireNonNull(fullName, "Имя не может быть null.");
        this.email = Objects.requireNonNull(email, "Email не может быть null.");
        this.phone = Objects.requireNonNull(phone, "Телефон не может быть null.");
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("User[#%d %s, email=%s]", id, fullName, email);
    }
}
