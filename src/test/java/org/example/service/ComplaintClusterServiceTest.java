package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.example.model.enums.ComplaintSubcategory;
import org.junit.jupiter.api.Test;

class ComplaintClusterServiceTest {

    private static final double LATITUDE = -22.252;
    private static final double LONGITUDE = -45.703;

    @Test
    void groupsNearbyReportsFromDifferentUsersAndCountsTheMapPin() {
        Complaint first = complaint("USR-001", ComplaintCategory.BURACO_RUA,
            ComplaintSubcategory.BURACO_EM_VIA, ComplaintPriority.MEDIA, atMetersEast(0));
        Complaint second = complaint("USR-002", ComplaintCategory.BURACO_RUA,
            ComplaintSubcategory.BURACO_EM_VIA, ComplaintPriority.ALTA, atMetersEast(12));
        Complaint third = complaint("USR-003", ComplaintCategory.BURACO_RUA,
            ComplaintSubcategory.BURACO_EM_VIA, ComplaintPriority.URGENTE, atMetersEast(34));
        Complaint fourth = complaint("USR-002", ComplaintCategory.BURACO_RUA,
            ComplaintSubcategory.BURACO_EM_VIA, ComplaintPriority.BAIXA, atMetersEast(-20));

        var clusters = ComplaintClusterService.groupByProximity(
            List.of(first, second, third, fourth));

        assertEquals(1, clusters.size());
        assertEquals(4, clusters.get(0).count());
        assertSame(third, clusters.get(0).highestPriorityComplaint());
    }

    @Test
    void keepsSameIssueReportsMoreThanFortyMetersApartAsSeparatePins() {
        Complaint nearby = complaint("USR-001", ComplaintCategory.BURACO_RUA,
            ComplaintSubcategory.BURACO_EM_VIA, ComplaintPriority.ALTA, atMetersEast(0));
        Complaint farAway = complaint("USR-002", ComplaintCategory.BURACO_RUA,
            ComplaintSubcategory.BURACO_EM_VIA, ComplaintPriority.URGENTE, atMetersEast(41));

        var clusters = ComplaintClusterService.groupByProximity(List.of(nearby, farAway));

        assertEquals(2, clusters.size());
        assertEquals(List.of(1, 1), clusters.stream()
            .map(ComplaintClusterService.ComplaintCluster::count).toList());
    }

    @Test
    void keepsNearbyReportsOfDifferentIssuesInSeparatePins() {
        Complaint roadHole = complaint("USR-001", ComplaintCategory.BURACO_RUA,
            ComplaintSubcategory.BURACO_EM_VIA, ComplaintPriority.ALTA, atMetersEast(0));
        Complaint damagedAsphalt = complaint("USR-002", ComplaintCategory.BURACO_RUA,
            ComplaintSubcategory.ASFALTO_DANIFICADO, ComplaintPriority.MEDIA, atMetersEast(5));
        Complaint darkStreet = complaint("USR-003", ComplaintCategory.ILUMINACAO_PUBLICA,
            ComplaintSubcategory.POSTE_APAGADO, ComplaintPriority.URGENTE, atMetersEast(8));

        var clusters = ComplaintClusterService.groupByProximity(
            List.of(roadHole, damagedAsphalt, darkStreet));

        assertEquals(3, clusters.size());
        assertTrue(clusters.stream().allMatch(cluster -> cluster.count() == 1));
    }

    @Test
    void preservesSingleAndUnlocatedReportsAsIndividualPins() {
        Complaint located = complaint("USR-001", ComplaintCategory.BURACO_RUA,
            ComplaintSubcategory.BURACO_EM_VIA, ComplaintPriority.MEDIA, atMetersEast(0));
        Complaint withoutLocation = complaint("USR-002", ComplaintCategory.BURACO_RUA,
            ComplaintSubcategory.BURACO_EM_VIA, ComplaintPriority.BAIXA, null);

        var clusters = ComplaintClusterService.groupByProximity(
            java.util.Arrays.asList(located, null, withoutLocation));

        assertEquals(2, clusters.size());
        assertTrue(clusters.stream().allMatch(cluster -> cluster.count() == 1));
    }

    @Test
    void handlesEmptyAndNullMapData() {
        assertTrue(ComplaintClusterService.groupByProximity(List.of()).isEmpty());
        assertTrue(ComplaintClusterService.groupByProximity(null).isEmpty());
    }

    private Complaint complaint(String creatorId, ComplaintCategory category,
            ComplaintSubcategory subcategory, ComplaintPriority priority, Location location) {
        return new Complaint(category, subcategory, location, "Mock de teste",
            ComplaintStatus.PENDENTE, priority, LocalDate.of(2026, 9, 21),
            creatorId, creatorId);
    }

    private Location atMetersEast(double meters) {
        double longitudeDelta = meters / (111_320.0 * Math.cos(Math.toRadians(LATITUDE)));
        return new Location(LATITUDE, LONGITUDE + longitudeDelta, "Ponto de teste");
    }
}
