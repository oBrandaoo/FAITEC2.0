package org.example.model;

import org.example.model.enums.UserStatus;
//TODO: enhance user model
public class User {
    private String id;
    private String name;
    private String password;
    private UserStatus status;

    public User(String id, String name, String password, UserStatus status) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.status = status;
    }
}
