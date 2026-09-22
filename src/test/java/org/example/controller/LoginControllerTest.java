package org.example.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.example.model.User;
import org.example.model.enums.UserRole;
import org.example.model.enums.UserStatus;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    void authenticatesEveryDefaultUserWithTheDemoPassword() {
        LoginController controller = new LoginController();
        List<DefaultAccount> defaultAccounts = List.of(
            new DefaultAccount("admin", "USR-001", UserRole.ADMINISTRADOR),
            new DefaultAccount("atendente", "USR-002", UserRole.ATENDENTE),
            new DefaultAccount("cidadao", "USR-003", UserRole.CIDADAO)
        );

        for (DefaultAccount account : defaultAccounts) {
            User authenticated = controller.authenticate(account.username(), "1234");

            assertNotNull(authenticated, account.username());
            assertEquals(account.id(), authenticated.getId());
            assertEquals(account.role(), authenticated.getRole());
            assertEquals(UserStatus.ATIVA, authenticated.getStatus());
        }
    }

    @Test
    void rejectsInvalidDefaultUserCredentials() {
        LoginController controller = new LoginController();

        assertNull(controller.authenticate("admin", "wrong"));
        assertNull(controller.authenticate("unknown", "1234"));
    }

    private record DefaultAccount(String username, String id, UserRole role) {
    }
}
