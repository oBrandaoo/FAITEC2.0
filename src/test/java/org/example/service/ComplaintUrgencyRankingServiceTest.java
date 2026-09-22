package org.example.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.example.model.enums.ComplaintSubcategory;
import org.junit.jupiter.api.Test;

class ComplaintUrgencyRankingServiceTest {

    @Test
    void ranksOpenProblemsByRecurrenceThenPriorityAndAge() {
        List<Complaint> complaints = new ArrayList<>();
        complaints.addAll(cluster(3, -22.2500, ComplaintPriority.MEDIA,
            ComplaintStatus.PENDENTE));
        complaints.addAll(cluster(2, -22.2520, ComplaintPriority.URGENTE,
            ComplaintStatus.PENDENTE));
        complaints.addAll(cluster(1, -22.2540, ComplaintPriority.ALTA,
            ComplaintStatus.EM_ANALISE));
        complaints.addAll(cluster(4, -22.2560, ComplaintPriority.URGENTE,
            ComplaintStatus.RESOLVIDO));

        var ranking = ComplaintUrgencyRankingService.topRecurring(complaints, 3);

        assertEquals(List.of(3, 2, 1), ranking.stream()
            .map(ComplaintUrgencyRankingService.RankedComplaint::recurrenceCount)
            .toList());
        assertEquals(ComplaintPriority.MEDIA, ranking.get(0).complaint().getPriority());
        assertEquals(ComplaintPriority.URGENTE, ranking.get(1).complaint().getPriority());
        assertTrue(ranking.stream().noneMatch(item ->
            item.complaint().getStatus() == ComplaintStatus.RESOLVIDO));
    }

    @Test
    void handlesEmptyInputAndInvalidLimit() {
        assertTrue(ComplaintUrgencyRankingService.topRecurring(null, 3).isEmpty());
        assertTrue(ComplaintUrgencyRankingService.topRecurring(List.of(), 3).isEmpty());
        assertTrue(ComplaintUrgencyRankingService.topRecurring(
            cluster(1, -22.25, ComplaintPriority.URGENTE, ComplaintStatus.PENDENTE), 0)
            .isEmpty());
    }

    private List<Complaint> cluster(int count, double latitude,
            ComplaintPriority priority, ComplaintStatus status) {
        List<Complaint> result = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            result.add(new Complaint(
                ComplaintCategory.BURACO_RUA,
                ComplaintSubcategory.BURACO_EM_VIA,
                new Location(latitude, -45.7000 + (index * 0.00001), "Ponto de teste"),
                "Problema recorrente",
                status,
                priority,
                LocalDate.of(2026, 9, 21).minusDays(index)
            ));
        }
        return result;
    }
}
