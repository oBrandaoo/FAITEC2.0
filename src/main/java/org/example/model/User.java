package org.example.model;

import org.example.model.enums.UserStatus;
import org.example.model.enums.UserRole;
//TODO: enhance user model
public class User {
    private final String id;
    private final String name;
    private final String password;
    private final UserStatus status;
    private final UserRole role;

    public User(String id, String name, String password, UserStatus status, UserRole role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.status = status;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserStatus getStatus() {
        return status;
    }

    public UserRole getRole() {
        return role;
    }

    public boolean passwordMatches(String candidate) {
        return password.equals(candidate);
    }
}
