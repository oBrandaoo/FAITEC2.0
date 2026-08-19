package org.example.controller.complaint;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import org.example.model.Complaint;
import org.example.model.ComplaintHistoryEntry;
import org.example.model.User;
import org.example.service.ComplaintService;
import org.example.util.MapDialog;
import org.example.util.UserSession;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class ComplaintTrackingController {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private ListView<Complaint> complaintsList;
    @FXML private ListView<String> historyList;
    @FXML private Label categoryLabel;
    @FXML private Label subcategoryLabel;
    @FXML private Label statusLabel;
    @FXML private Label priorityLabel;
    @FXML private Label addressLabel;
    @FXML private Label registrationDateLabel;
    @FXML private Label lastUpdateLabel;
    @FXML private Label trackingDescriptionLabel;
    @FXML private Label ownershipLabel;
    @FXML private ProgressBar progressBar;
    @FXML private TextArea descriptionArea;
    @FXML private Button mapButton;
    @FXML private VBox trackingDetails;
    @FXML private Label emptyLabel;

    private Complaint selectedComplaint;

    @FXML
    public void initialize() {
        configureComplaintList();
        loadComplaints();
    }

    private void configureComplaintList() {
        complaintsList.setCellFactory(list -> new ListCell<>() {
            private final Label category = new Label();
            private final Label metadata = new Label();
            private final Label address = new Label();
            private final VBox content = new VBox(5, category, metadata, address);

            {
                category.getStyleClass().add("tracking-list-category");
                metadata.getStyleClass().add("tracking-list-metadata");
                address.getStyleClass().add("tracking-list-address");
                address.setWrapText(true);
                content.getStyleClass().add("tracking-list-content");
            }

            @Override
            protected void updateItem(Complaint complaint, boolean empty) {
                super.updateItem(complaint, empty);
                if (empty || complaint == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                category.setText(complaint.getCategory().toString());
                metadata.setText(complaint.getStatus() + "  •  "
                        + DATE_FORMAT.format(complaint.getDate()));
                address.setText(complaint.getLocation().getAddress());
                setAccessibleText(complaint.getCategory() + ", "
                        + complaint.getStatus() + ", "
                        + complaint.getLocation().getAddress());
                setText(null);
                setGraphic(content);
            }
        });

        complaintsList.getSelectionModel().selectedItemProperty().addListener(
                (observable, previous, selected) -> {
                    if (selected != null) {
                        displayComplaint(selected);
                    }
                }
        );
    }

    private void loadComplaints() {
        var complaints = ComplaintService
                .getTrackableComplaints(UserSession.getLoggedUser())
                .stream()
                .sorted(Comparator.comparing(Complaint::getDate).reversed())
                .toList();

        complaintsList.setItems(FXCollections.observableArrayList(complaints));
        boolean empty = complaints.isEmpty();
        emptyLabel.setVisible(empty);
        emptyLabel.setManaged(empty);
        trackingDetails.setVisible(!empty);
        trackingDetails.setManaged(!empty);

        if (!empty) {
            complaintsList.getSelectionModel().selectFirst();
        }
    }

    private void displayComplaint(Complaint complaint) {
        selectedComplaint = complaint;
        categoryLabel.setText(complaint.getCategory().toString());
        subcategoryLabel.setText(complaint.getSubcategory() == null
                ? "Não informada"
                : complaint.getSubcategory().toString());
        statusLabel.setText(complaint.getStatus().toString());
        priorityLabel.setText(complaint.getPriority().toString());
        addressLabel.setText(complaint.getLocation().getAddress());
        registrationDateLabel.setText(DATE_FORMAT.format(complaint.getDate()));
        descriptionArea.setText(complaint.getDescription());
        trackingDescriptionLabel.setText(complaint.getStatus().getTrackingDescription());
        progressBar.setProgress(complaint.getStatus().getTrackingProgress());
        updateStatusStyle(complaint);
        updateOwnership(complaint);
        updateLastChange(complaint);
        loadHistory(complaint);
        mapButton.setDisable(false);
    }

    private void updateStatusStyle(Complaint complaint) {
        statusLabel.getStyleClass().removeIf(style -> style.startsWith("tracking-status-"));
        statusLabel.getStyleClass().add(
                "tracking-status-" + complaint.getStatus().name().toLowerCase()
        );
    }

    private void updateOwnership(Complaint complaint) {
        User user = UserSession.getLoggedUser();
        boolean ownComplaint = user != null && user.getId().equals(complaint.getCreatorId());
        ownershipLabel.setText(ownComplaint ? "Seu relato" : "Relato público");
        ownershipLabel.getStyleClass().removeAll("tracking-ownership-own", "tracking-ownership-public");
        ownershipLabel.getStyleClass().add(
                ownComplaint ? "tracking-ownership-own" : "tracking-ownership-public"
        );
    }

    private void updateLastChange(Complaint complaint) {
        LocalDateTime lastUpdate = complaint.getHistory().stream()
                .map(ComplaintHistoryEntry::getChangedAt)
                .max(LocalDateTime::compareTo)
                .orElse(complaint.getDate().atStartOfDay());
        lastUpdateLabel.setText("Última atualização: " + DATE_TIME_FORMAT.format(lastUpdate));
    }

    private void loadHistory(Complaint complaint) {
        var history = complaint.getHistory().stream()
                .sorted(Comparator.comparing(ComplaintHistoryEntry::getChangedAt).reversed())
                .map(this::formatHistoryEntry)
                .toList();
        historyList.setItems(FXCollections.observableArrayList(history));
    }

    private String formatHistoryEntry(ComplaintHistoryEntry entry) {
        String transition = entry.getPreviousStatus() == null
                ? entry.getNewStatus().toString()
                : entry.getPreviousStatus() == entry.getNewStatus()
                        ? "Atualização do relato"
                        : entry.getPreviousStatus() + "  →  " + entry.getNewStatus();
        String note = entry.getNote().isBlank() ? "" : "\n" + entry.getNote();
        return DATE_TIME_FORMAT.format(entry.getChangedAt())
                + "  •  " + entry.getResponsible()
                + "\n" + transition
                + note;
    }

    @FXML
    private void showOnMap() {
        if (selectedComplaint != null) {
            MapDialog.showLocation(
                    selectedComplaint.getLocation(),
                    mapButton.getScene().getWindow()
            );
        }
    }
}
