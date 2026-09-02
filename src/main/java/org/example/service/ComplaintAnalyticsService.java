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

        List<Complaint> critical = open.stream()
            .sorted(Comparator.comparingInt((Complaint item) -> riskScore(item, referenceDate)).reversed()
                .thenComparing(Complaint::getDate, Comparator.nullsLast(Comparator.naturalOrder())))
            .limit(4)
            .toList();

        return new AnalyticsSummary(resolutionRate, averageOpenAge, open.size(), critical,
            buildInsights(complaints, open, critical, referenceDate));
    }

    private static List<String> buildInsights(List<Complaint> all, List<Complaint> open,
            List<Complaint> critical, LocalDate today) {
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

        if (!critical.isEmpty()) {
            Complaint first = critical.get(0);
            insights.add("Próxima prioridade sugerida: " + first.getCategory() + " — "
                + first.getPriority().toString().toLowerCase() + ", aberta há "
                + ageInDays(first, today) + " dia(s)." );
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
