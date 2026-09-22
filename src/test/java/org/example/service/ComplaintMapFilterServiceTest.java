package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.example.controller.LoginController;
import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.User;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.example.model.enums.ComplaintSubcategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ComplaintMapFilterServiceTest {

    private List<User> defaultUsers;
    private List<Complaint> complaints;

    @BeforeEach
    void setUp() {
        defaultUsers = LoginController.getAvailableUsers();
        complaints = List.of(
            complaint(defaultUsers.get(0), ComplaintPriority.URGENTE, -22.250),
            complaint(defaultUsers.get(1), ComplaintPriority.ALTA, -22.252),
            complaint(defaultUsers.get(2), ComplaintPriority.URGENTE, -22.254)
        );
    }

    @Test
    void allDefaultUsersCanFilterTheMapToTheirOwnReports() {
        for (User user : defaultUsers) {
            List<Complaint> result = ComplaintMapFilterService.filter(
                complaints, user, true, null);

            assertEquals(1, result.size());
            assertEquals(user.getId(), result.get(0).getCreatorId());
        }
    }

    @Test
    void allScopeShowsReportsFromEveryDefaultUser() {
        List<Complaint> result = ComplaintMapFilterService.filter(
            complaints, defaultUsers.get(0), false, null);

        assertEquals(3, result.size());
    }

    @Test
    void priorityFilterCanBeCombinedWithOwnReports() {
        User citizen = defaultUsers.get(2);
        List<Complaint> result = ComplaintMapFilterService.filter(
            complaints, citizen, true, ComplaintPriority.URGENTE);

        assertEquals(1, result.size());
        assertEquals(citizen.getId(), result.get(0).getCreatorId());
        assertEquals(ComplaintPriority.URGENTE, result.get(0).getPriority());
    }

    @Test
    void mineFilterWithoutALoggedInUserShowsNoReports() {
        assertTrue(ComplaintMapFilterService.filter(complaints, null, true, null).isEmpty());
    }

    private Complaint complaint(User creator, ComplaintPriority priority, double latitude) {
        return new Complaint(
            ComplaintCategory.BURACO_RUA,
            ComplaintSubcategory.BURACO_EM_VIA,
            new Location(latitude, -45.703, "Centro"),
            "Teste",
            ComplaintStatus.PENDENTE,
            priority,
            LocalDate.of(2026, 9, 21),
            creator.getId(),
            creator.getName()
        );
    }
}
