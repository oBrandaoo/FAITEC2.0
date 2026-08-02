package org.example.util;

import org.example.model.User;
import org.example.model.enums.UserRole;
import org.example.model.enums.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserSessionTest {

    @AfterEach
    void clearSession() {
        UserSession.logout();
    }

    @Test
    void shouldStartLoggedOut() {
        UserSession.logout();

        assertFalse(UserSession.isLoggedIn());
        assertNull(UserSession.getLoggedUser());
    }

    @Test
    void shouldStoreAuthenticatedUser() {
        User user = user();

        UserSession.login(user);

        assertTrue(UserSession.isLoggedIn());
        assertSame(user, UserSession.getLoggedUser());
    }

    @Test
    void shouldRejectNullLogin() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> UserSession.login(null)
        );

        assertEquals("O usuário não pode ser nulo.", exception.getMessage());
        assertFalse(UserSession.isLoggedIn());
    }

    @Test
    void shouldClearUserOnLogout() {
        UserSession.login(user());

        UserSession.logout();

        assertFalse(UserSession.isLoggedIn());
        assertNull(UserSession.getLoggedUser());
    }

    private User user() {
        return new User(
                "USR-003",
                "Cidadão",
                "1234",
                UserStatus.ATIVA,
                UserRole.CIDADAO
        );
    }
}
