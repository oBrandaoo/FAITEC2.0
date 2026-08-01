package org.example.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import org.example.model.Complaint;
import org.example.model.User;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.example.service.ComplaintService;
import org.example.util.UserSession;
import org.example.util.AccessibilityManager;

import java.util.Comparator;
import java.util.List;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalLabel;
    @FXML private Label pendingLabel;
    @FXML private Label inProgressLabel;
    @FXML private Label resolvedLabel;
    @FXML private Label urgentLabel;
    @FXML private Label resolutionRateLabel;
    @FXML private ProgressBar resolutionProgress;
    @FXML private PieChart categoryChart;
    @FXML private BarChart<String, Number> priorityChart;
    @FXML private ListView<String> urgentList;

    @FXML
    public void initialize() {
        boolean animationsEnabled = !AccessibilityManager.isReducedMotion();
        categoryChart.setAnimated(animationsEnabled);
        priorityChart.setAnimated(animationsEnabled);
        configureWelcome();
        loadDashboard();
    }

    private void configureWelcome() {
        User user = UserSession.getLoggedUser();
        welcomeLabel.setText(
                user == null ? "Visão geral" : "Olá, " + user.getName()
        );
    }

    private void loadDashboard() {
        List<Complaint> complaints = ComplaintService.getAllComplaints();

        long pending = countStatus(complaints, ComplaintStatus.PENDENTE);
        long inProgress = countStatus(complaints, ComplaintStatus.EM_ANALISE)
                + countStatus(complaints, ComplaintStatus.EM_EXECUCAO);
        long resolved = countStatus(complaints, ComplaintStatus.RESOLVIDO);
        long urgent = complaints.stream()
                .filter(item -> item.getPriority() == ComplaintPriority.URGENTE)
                .count();

        totalLabel.setText(String.valueOf(complaints.size()));
        pendingLabel.setText(String.valueOf(pending));
        inProgressLabel.setText(String.valueOf(inProgress));
        resolvedLabel.setText(String.valueOf(resolved));
        urgentLabel.setText(String.valueOf(urgent));

        double rate = complaints.isEmpty()
                ? 0
                : (double) resolved / complaints.size();
        resolutionProgress.setProgress(rate);
        resolutionRateLabel.setText(String.format("%.0f%%", rate * 100));

        loadCategoryChart(complaints);
        loadPriorityChart(complaints);
        loadUrgentList(complaints);
    }

    private long countStatus(List<Complaint> complaints, ComplaintStatus status) {
        return complaints.stream()
                .filter(item -> item.getStatus() == status)
                .count();
    }

    private void loadCategoryChart(List<Complaint> complaints) {
        var data = FXCollections.<PieChart.Data>observableArrayList();
        for (ComplaintCategory category : ComplaintCategory.values()) {
            long count = complaints.stream()
                    .filter(item -> item.getCategory() == category)
                    .count();
            if (count > 0) {
                data.add(new PieChart.Data(category.toString(), count));
            }
        }
        categoryChart.setData(data);
        categoryChart.setLabelsVisible(true);
    }

    private void loadPriorityChart(List<Complaint> complaints) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reclamações");

        for (ComplaintPriority priority : ComplaintPriority.values()) {
            long count = complaints.stream()
                    .filter(item -> item.getPriority() == priority)
                    .count();
            series.getData().add(new XYChart.Data<>(priority.toString(), count));
        }

        priorityChart.getData().clear();
        priorityChart.getData().add(series);
        priorityChart.setLegendVisible(false);
    }

    private void loadUrgentList(List<Complaint> complaints) {
        List<String> items = complaints.stream()
                .filter(item -> item.getPriority() == ComplaintPriority.URGENTE)
                .sorted(Comparator.comparing(Complaint::getDate).reversed())
                .limit(5)
                .map(item -> item.getDate()
                        + "  •  "
                        + item.getCategory()
                        + "\n"
                        + item.getLocation().getAddress())
                .toList();

        urgentList.setItems(FXCollections.observableArrayList(items));
        urgentList.setPlaceholder(new Label("Nenhuma reclamação urgente."));
    }
}
