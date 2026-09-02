package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDate;
import java.util.List;

import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.junit.jupiter.api.Test;

class ComplaintAnalyticsServiceTest {

    @Test
    void calculatesOperationalIndicatorsAndPrioritizesRisk() {
        LocalDate today = LocalDate.of(2026, 9, 1);
        Complaint oldUrgent = complaint(ComplaintStatus.PENDENTE, ComplaintPriority.URGENTE,
            today.minusDays(10));
        Complaint recentLow = complaint(ComplaintStatus.EM_ANALISE, ComplaintPriority.BAIXA,
            today.minusDays(2));
        Complaint resolved = complaint(ComplaintStatus.RESOLVIDO, ComplaintPriority.MEDIA,
            today.minusDays(5));
        Complaint canceled = complaint(ComplaintStatus.CANCELADO, ComplaintPriority.MEDIA,
            today.minusDays(3));

        var result = ComplaintAnalyticsService.analyze(
            List.of(recentLow, resolved, oldUrgent, canceled), today);

        assertEquals(50.0, result.resolutionRate());
        assertEquals(6.0, result.averageOpenAge());
        assertEquals(2, result.openCount());
        assertEquals(oldUrgent, result.criticalComplaints().get(0));
        assertFalse(result.insights().isEmpty());
    }

    @Test
    void handlesEmptyInput() {
        var result = ComplaintAnalyticsService.analyze(List.of(), LocalDate.of(2026, 9, 1));
        assertEquals(0, result.openCount());
        assertEquals(0.0, result.resolutionRate());
        assertFalse(result.insights().isEmpty());
    }

    private Complaint complaint(ComplaintStatus status, ComplaintPriority priority, LocalDate date) {
        return new Complaint(ComplaintCategory.BURACO_RUA, null,
            new Location(-22.25, -45.70, "Centro"), "Teste", status, priority, date);
    }
}
