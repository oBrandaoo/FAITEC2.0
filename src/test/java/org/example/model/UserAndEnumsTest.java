package org.example.model;

import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.example.model.enums.ComplaintSubcategory;
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
        assertTrue(role.canViewMap());
    }

    @Test
    void enumsShouldExposeLabelsUsedByInterface() {
        assertAll(
                () -> assertEquals("Administrador", UserRole.ADMINISTRADOR.toString()),
                () -> assertEquals("Ativa", UserStatus.ATIVA.toString()),
                () -> assertEquals("Buraco na rua", ComplaintCategory.BURACO_RUA.toString()),
                () -> assertEquals("Trânsito e mobilidade", ComplaintCategory.TRANSITO_MOBILIDADE.toString()),
                () -> assertEquals("Semáforo com defeito", ComplaintSubcategory.SEMAFORO_DEFEITO.toString()),
                () -> assertEquals("Urgente", ComplaintPriority.URGENTE.toString()),
                () -> assertEquals("Em análise", ComplaintStatus.EM_ANALISE.toString())
        );
    }

    @Test
    void subcategoriesShouldBeGroupedByCategory() {
        var trafficSubcategories = ComplaintSubcategory.forCategory(
                ComplaintCategory.TRANSITO_MOBILIDADE
        );

        assertEquals(5, trafficSubcategories.size());
        assertTrue(trafficSubcategories.stream().allMatch(subcategory ->
                subcategory.belongsTo(ComplaintCategory.TRANSITO_MOBILIDADE)
        ));
        assertTrue(ComplaintSubcategory.forCategory(null).isEmpty());
    }

    @Test
    void complaintStatusesShouldExposeTrackingProgress() {
        assertAll(
                () -> assertEquals(0.20, ComplaintStatus.PENDENTE.getTrackingProgress()),
                () -> assertEquals(0.45, ComplaintStatus.EM_ANALISE.getTrackingProgress()),
                () -> assertEquals(0.75, ComplaintStatus.EM_EXECUCAO.getTrackingProgress()),
                () -> assertEquals(1.00, ComplaintStatus.RESOLVIDO.getTrackingProgress()),
                () -> assertFalse(ComplaintStatus.PENDENTE.getTrackingDescription().isBlank()),
                () -> assertFalse(ComplaintStatus.CANCELADO.getTrackingDescription().isBlank())
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
