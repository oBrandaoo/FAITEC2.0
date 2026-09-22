package org.example.util;

import java.util.List;

import org.example.model.Complaint;
import org.example.model.enums.ComplaintCategory;
import org.example.service.ComplaintUrgencyRankingService.RankedComplaint;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

/** Builds the shared top-three urgency cards used by both home pages. */
public final class UrgentComplaintCardFactory {

    private UrgentComplaintCardFactory() {
    }

    public static void populate(HBox container, List<RankedComplaint> ranking) {
        container.getChildren().clear();
        if (ranking == null || ranking.isEmpty()) {
            Label empty = new Label("Não há reclamações abertas para priorizar.");
            empty.getStyleClass().add("urgent-ranking-empty");
            container.getChildren().add(empty);
            return;
        }

        for (int index = 0; index < ranking.size(); index++) {
            VBox card = createCard(ranking.get(index), index + 1);
            HBox.setHgrow(card, Priority.ALWAYS);
            container.getChildren().add(card);
        }
    }

    private static VBox createCard(RankedComplaint ranked, int position) {
        Complaint complaint = ranked.complaint();

        Label positionLabel = new Label("#" + position);
        positionLabel.getStyleClass().add("urgent-card-position");

        Label icon = new Label(iconFor(complaint.getCategory()));
        icon.getStyleClass().add("urgent-card-icon");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        String recurrenceText = ranked.recurrenceCount() == 1
            ? "1 registro"
            : ranked.recurrenceCount() + " recorrências";
        Label recurrence = new Label(recurrenceText);
        recurrence.getStyleClass().add("urgent-card-recurrence");

        HBox header = new HBox(8, positionLabel, icon, spacer, recurrence);
        header.getStyleClass().add("urgent-card-header");

        Label category = new Label(complaint.getCategory().toString());
        category.setWrapText(true);
        category.getStyleClass().add("urgent-card-category");

        String detailText = complaint.getSubcategory() == null
            ? complaint.getDescription()
            : complaint.getSubcategory().toString();
        Label detail = new Label(detailText);
        detail.setWrapText(true);
        detail.getStyleClass().add("urgent-card-detail");

        Label priority = new Label("Prioridade " + complaint.getPriority());
        priority.getStyleClass().addAll(
            "urgent-card-priority",
            "urgent-priority-" + complaint.getPriority().name().toLowerCase());

        String addressText = complaint.getLocation() == null
            ? "Localização não informada"
            : complaint.getLocation().getAddress();
        Label address = new Label(addressText);
        address.setWrapText(true);
        address.getStyleClass().add("urgent-card-address");

        VBox card = new VBox(7, header, category, detail, priority, address);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setMinWidth(0);
        card.getStyleClass().add("urgent-ranking-item");
        card.setAccessibleText(position + "º lugar. " + complaint.getCategory()
            + ". " + recurrenceText + ". Prioridade " + complaint.getPriority() + ".");
        return card;
    }

    private static String iconFor(ComplaintCategory category) {
        return switch (category) {
            case BURACO_RUA -> "⚠";
            case ILUMINACAO_PUBLICA -> "💡";
            case LIX0_ACUMULADO -> "♻";
            case ESGOTO -> "💧";
            case SEGURANCA -> "◆";
            case TRANSITO_MOBILIDADE -> "🚦";
        };
    }
}
