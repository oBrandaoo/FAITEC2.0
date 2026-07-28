package org.example.util;

import org.example.model.User;

public final class UserSession {
    private static User loggedUser;

    private UserSession() {
    }

    public static void login(User user) {
        if (user == null) {
            throw new IllegalArgumentException("O usuário não pode ser nulo.");
        }
        loggedUser = user;
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    public static boolean isLoggedIn() {
        return loggedUser != null;
    }

    public static void logout() {
        loggedUser = null;
    }
}
