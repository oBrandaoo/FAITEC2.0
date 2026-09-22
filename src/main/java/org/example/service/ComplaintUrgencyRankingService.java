package org.example.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.example.model.Complaint;
import org.example.model.enums.ComplaintStatus;
import org.example.service.ComplaintClusterService.ComplaintCluster;

/** Ranks recurring open problems for the home-page urgency cards. */
public final class ComplaintUrgencyRankingService {

    private ComplaintUrgencyRankingService() {
    }

    public static List<RankedComplaint> topRecurring(List<Complaint> source, int limit) {
        if (source == null || source.isEmpty() || limit <= 0) {
            return List.of();
        }

        List<Complaint> openComplaints = source.stream()
            .filter(Objects::nonNull)
            .filter(ComplaintUrgencyRankingService::isOpen)
            .toList();

        return ComplaintClusterService.groupByProximity(openComplaints).stream()
            .map(cluster -> new RankedComplaint(
                mostUrgentComplaint(cluster), cluster.count()))
            .sorted(Comparator.comparingInt(RankedComplaint::recurrenceCount).reversed()
                .thenComparing(Comparator.comparingInt(
                    (RankedComplaint ranked) -> priorityRank(ranked.complaint())).reversed())
                .thenComparing(ranked -> ranked.complaint().getDate(),
                    Comparator.nullsLast(Comparator.naturalOrder())))
            .limit(limit)
            .toList();
    }

    private static boolean isOpen(Complaint complaint) {
        return complaint.getStatus() != ComplaintStatus.RESOLVIDO
            && complaint.getStatus() != ComplaintStatus.CANCELADO;
    }

    private static Complaint mostUrgentComplaint(ComplaintCluster cluster) {
        return cluster.complaints().stream()
            .sorted(Comparator.comparingInt(ComplaintUrgencyRankingService::priorityRank)
                .reversed()
                .thenComparing(Complaint::getDate,
                    Comparator.nullsLast(Comparator.naturalOrder())))
            .findFirst()
            .orElseThrow();
    }

    private static int priorityRank(Complaint complaint) {
        return complaint.getPriority() == null ? -1 : complaint.getPriority().ordinal();
    }

    public record RankedComplaint(Complaint complaint, int recurrenceCount) {
        public RankedComplaint {
            Objects.requireNonNull(complaint, "A reclamação representativa é obrigatória.");
            if (recurrenceCount < 1) {
                throw new IllegalArgumentException("A recorrência deve ser positiva.");
            }
        }
    }
}
