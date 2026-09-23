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
        ComplaintPriority[] priorities = {
            ComplaintPriority.URGENTE,
            ComplaintPriority.ALTA,
            ComplaintPriority.URGENTE,
            ComplaintPriority.MEDIA
        };
        complaints = java.util.stream.IntStream.range(0, defaultUsers.size())
            .mapToObj(index -> complaint(
                defaultUsers.get(index), priorities[index % priorities.length],
                -22.250 - index * 0.002))
            .toList();
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

        assertEquals(defaultUsers.size(), result.size());
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
    void statusAndDateFiltersCanBeCombined() {
        User creator = defaultUsers.get(2);
        Complaint recentResolved = complaint(creator, ComplaintPriority.MEDIA, -22.250,
                ComplaintStatus.RESOLVIDO, LocalDate.now().minusDays(2));
        Complaint oldResolved = complaint(creator, ComplaintPriority.MEDIA, -22.252,
                ComplaintStatus.RESOLVIDO, LocalDate.now().minusDays(20));
        Complaint recentOpen = complaint(creator, ComplaintPriority.MEDIA, -22.254,
                ComplaintStatus.PENDENTE, LocalDate.now().minusDays(1));

        List<Complaint> result = ComplaintMapFilterService.filter(
                List.of(recentResolved, oldResolved, recentOpen), creator, false, null,
                ComplaintStatus.RESOLVIDO, LocalDate.now().minusDays(7), LocalDate.now());

        assertEquals(List.of(recentResolved), result);
    }

    @Test
    void mineFilterWithoutALoggedInUserShowsNoReports() {
        assertTrue(ComplaintMapFilterService.filter(complaints, null, true, null).isEmpty());
    }

    private Complaint complaint(User creator, ComplaintPriority priority, double latitude) {
        return complaint(creator, priority, latitude, ComplaintStatus.PENDENTE,
                LocalDate.of(2026, 9, 21));
    }

    private Complaint complaint(User creator, ComplaintPriority priority, double latitude,
            ComplaintStatus status, LocalDate date) {
        return new Complaint(
            ComplaintCategory.BURACO_RUA,
            ComplaintSubcategory.BURACO_EM_VIA,
            new Location(latitude, -45.703, "Centro"),
            "Teste",
            status,
            priority,
            date,
            creator.getId(),
            creator.getName()
        );
    }
}
