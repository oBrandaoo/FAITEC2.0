package org.example.model;

import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.example.model.enums.UserRole;
import org.example.model.enums.UserStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAndEnumsTest {

    @Test
    void shouldExposeUserDataAndValidatePassword() {
        User user = new User(
                "USR-001",
                "Administrador",
                "1234",
                UserStatus.ATIVA,
                UserRole.ADMINISTRADOR
        );

        assertAll(
                () -> assertEquals("USR-001", user.getId()),
                () -> assertEquals("Administrador", user.getName()),
                () -> assertEquals(UserStatus.ATIVA, user.getStatus()),
                () -> assertEquals(UserRole.ADMINISTRADOR, user.getRole()),
                () -> assertTrue(user.passwordMatches("1234")),
                () -> assertFalse(user.passwordMatches("senha-incorreta"))
        );
    }

    @Test
    void shouldApplyAdministratorPermissions() {
        UserRole role = UserRole.ADMINISTRADOR;

        assertTrue(role.canCreateComplaint());
        assertTrue(role.canManageComplaints());
        assertTrue(role.canViewMap());
    }

    @Test
    void shouldApplyAttendantPermissions() {
        UserRole role = UserRole.ATENDENTE;

        assertFalse(role.canCreateComplaint());
        assertTrue(role.canManageComplaints());
        assertTrue(role.canViewMap());
    }

    @Test
    void shouldApplyCitizenPermissions() {
        UserRole role = UserRole.CIDADAO;

        assertTrue(role.canCreateComplaint());
        assertFalse(role.canManageComplaints());
        assertFalse(role.canViewMap());
    }

    @Test
    void enumsShouldExposeLabelsUsedByInterface() {
        assertAll(
                () -> assertEquals("Administrador", UserRole.ADMINISTRADOR.toString()),
                () -> assertEquals("Ativa", UserStatus.ATIVA.toString()),
                () -> assertEquals("Buraco na rua", ComplaintCategory.BURACO_RUA.toString()),
                () -> assertEquals("Urgente", ComplaintPriority.URGENTE.toString()),
                () -> assertEquals("Em análise", ComplaintStatus.EM_ANALISE.toString())
        );
    }

    @Test
    void locationShouldSupportConstructionAndEditing() {
        Location location = new Location(-22.25, -45.70, "Endereço inicial");

        location.setLatitude(-22.26);
        location.setLongitude(-45.71);
        location.setAddress("Novo endereço");

        assertAll(
                () -> assertEquals(-22.26, location.getLatitude()),
                () -> assertEquals(-45.71, location.getLongitude()),
                () -> assertEquals("Novo endereço", location.getAddress())
        );
    }
}
