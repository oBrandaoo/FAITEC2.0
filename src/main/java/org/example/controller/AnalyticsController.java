package org.example.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.example.model.Complaint;
import org.example.service.ComplaintAnalyticsService;
import org.example.service.ComplaintAnalyticsService.AnalyticsSummary;
import org.example.service.ComplaintService;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class AnalyticsController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Label resolutionRateLabel;
    @FXML private Label averageAgeLabel;
    @FXML private Label openQueueLabel;
    @FXML private Label updatedAtLabel;
    @FXML private ListView<String> insightsList;
    @FXML private ListView<Complaint> criticalList;

    @FXML
    public void initialize() {
        configureCriticalList();
        loadAnalysis();
    }

    private void loadAnalysis() {
        LocalDate today = LocalDate.now();
        AnalyticsSummary summary = ComplaintAnalyticsService.analyze(
            ComplaintService.getAllComplaints(), today);

        resolutionRateLabel.setText(String.format("%.0f%%", summary.resolutionRate()));
        averageAgeLabel.setText(String.format("%.1f dias", summary.averageOpenAge()));
        openQueueLabel.setText(String.valueOf(summary.openCount()));
        updatedAtLabel.setText("Atualizado em " + today.format(DATE_FORMAT));
        insightsList.setItems(FXCollections.observableArrayList(summary.insights()));
        criticalList.setItems(FXCollections.observableArrayList(summary.criticalComplaints()));
    }

    private void configureCriticalList() {
        criticalList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Complaint item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                String address = item.getLocation() == null
                    ? "Local não informado"
                    : item.getLocation().getAddress();
                setText(item.getPriority() + "  •  " + item.getCategory() + "\n"
                    + item.getStatus() + "  •  " + address);
            }
        });
    }
}
