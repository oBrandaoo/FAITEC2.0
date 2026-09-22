package org.example.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.model.Complaint;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.example.service.ComplaintClusterService.ComplaintCluster;

/** Produces local, explainable indicators from the complaints already in memory. */
public final class ComplaintAnalyticsService {

    private ComplaintAnalyticsService() {
    }

    public static AnalyticsSummary analyze(List<Complaint> source, LocalDate today) {
        List<Complaint> complaints = source == null ? List.of() : source;
        LocalDate referenceDate = today == null ? LocalDate.now() : today;

        List<Complaint> open = complaints.stream()
            .filter(ComplaintAnalyticsService::isOpen)
            .toList();
        long resolved = complaints.stream()
            .filter(item -> item.getStatus() == ComplaintStatus.RESOLVIDO)
            .count();
        long concluded = complaints.stream()
            .filter(item -> item.getStatus() == ComplaintStatus.RESOLVIDO
                || item.getStatus() == ComplaintStatus.CANCELADO)
            .count();
        double resolutionRate = concluded == 0 ? 0 : (resolved * 100.0) / concluded;
        double averageOpenAge = open.stream()
            .filter(item -> item.getDate() != null)
            .mapToLong(item -> Math.max(0, ChronoUnit.DAYS.between(item.getDate(), referenceDate)))
            .average().orElse(0);

        List<ComplaintCluster> criticalClusters = ComplaintClusterService.groupByProximity(open).stream()
            .sorted(Comparator.comparingInt((ComplaintCluster cluster) -> clusterRiskScore(cluster, referenceDate))
                .reversed()
                .thenComparing(Comparator.comparingInt(ComplaintCluster::count).reversed())
                .thenComparing(ComplaintAnalyticsService::oldestDate,
                    Comparator.nullsLast(Comparator.naturalOrder())))
            .limit(4)
            .toList();
        List<Complaint> critical = criticalClusters.stream()
            .map(cluster -> mostCriticalComplaint(cluster, referenceDate))
            .toList();

        return new AnalyticsSummary(resolutionRate, averageOpenAge, open.size(), critical,
            buildInsights(complaints, open, criticalClusters, referenceDate));
    }

    private static List<String> buildInsights(List<Complaint> all, List<Complaint> open,
            List<ComplaintCluster> criticalClusters, LocalDate today) {
        List<String> insights = new ArrayList<>();

        Map<ComplaintCategory, Long> openByCategory = open.stream()
            .collect(Collectors.groupingBy(Complaint::getCategory, Collectors.counting()));
        openByCategory.entrySet().stream().max(Map.Entry.comparingByValue()).ifPresent(entry ->
            insights.add("Maior demanda aberta: " + entry.getKey() + " (" + entry.getValue()
                + (entry.getValue() == 1 ? " registro)." : " registros).")));

        long urgentPending = open.stream()
            .filter(item -> item.getPriority() == ComplaintPriority.URGENTE)
            .filter(item -> item.getStatus() == ComplaintStatus.PENDENTE)
            .count();
        if (urgentPending > 0) {
            insights.add("Ação imediata: " + urgentPending + (urgentPending == 1
                ? " demanda urgente ainda aguarda triagem."
                : " demandas urgentes ainda aguardam triagem."));
        }

        long overdue = open.stream()
            .filter(item -> ageInDays(item, today) >= 7)
            .count();
        if (overdue > 0) {
            insights.add("Risco de atraso: " + overdue + (overdue == 1
                ? " demanda está aberta há 7 dias ou mais."
                : " demandas estão abertas há 7 dias ou mais."));
        }

        if (!criticalClusters.isEmpty()) {
            ComplaintCluster cluster = criticalClusters.get(0);
            Complaint first = mostCriticalComplaint(cluster, today);
            String groupedCount = cluster.count() > 1
                ? " (" + cluster.count() + " registros abertos próximos)"
                : "";
            insights.add("Próxima prioridade sugerida: " + first.getCategory() + " — "
                + first.getPriority().toString().toLowerCase() + ", aberta há "
                + ageInDays(first, today) + " dia(s)" + groupedCount + ".");
        }
        if (all.isEmpty()) {
            insights.add("Ainda não há dados suficientes para gerar recomendações.");
        }
        return List.copyOf(insights);
    }

    private static boolean isOpen(Complaint item) {
        return item != null && item.getStatus() != ComplaintStatus.RESOLVIDO
            && item.getStatus() != ComplaintStatus.CANCELADO;
    }

    private static int riskScore(Complaint item, LocalDate today) {
        int priorityWeight = switch (item.getPriority()) {
            case URGENTE -> 40;
            case ALTA -> 25;
            case MEDIA -> 12;
            case BAIXA -> 4;
        };
        int statusWeight = item.getStatus() == ComplaintStatus.PENDENTE ? 15 : 5;
        return priorityWeight + statusWeight + (int) Math.min(30, ageInDays(item, today) * 2);
    }

    private static int clusterRiskScore(ComplaintCluster cluster, LocalDate today) {
        return cluster.complaints().stream()
            .mapToInt(item -> riskScore(item, today))
            .max().orElse(0);
    }

    private static LocalDate oldestDate(ComplaintCluster cluster) {
        return cluster.complaints().stream()
            .map(Complaint::getDate)
            .filter(java.util.Objects::nonNull)
            .min(Comparator.naturalOrder())
            .orElse(null);
    }

    private static Complaint mostCriticalComplaint(ComplaintCluster cluster, LocalDate today) {
        return cluster.complaints().stream()
            .max(Comparator.comparingInt((Complaint item) -> riskScore(item, today)))
            .orElseThrow();
    }

    private static long ageInDays(Complaint item, LocalDate today) {
        if (item.getDate() == null) {
            return 0;
        }
        return Math.max(0, ChronoUnit.DAYS.between(item.getDate(), today));
    }

    public record AnalyticsSummary(double resolutionRate, double averageOpenAge, int openCount,
            List<Complaint> criticalComplaints, List<String> insights) {
    }
}
