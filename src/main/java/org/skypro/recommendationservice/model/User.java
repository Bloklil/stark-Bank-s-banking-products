package org.skypro.recommendationservice.model;

import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private Long telegramUserId;

    public User(UUID id, String username, String firstName, String lastName, Long telegramUserId) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.telegramUserId = telegramUserId;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Long getTelegramUserId() { return telegramUserId; }
    public void setTelegramUserId(Long telegramUserId) { this.telegramUserId = telegramUserId; }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
