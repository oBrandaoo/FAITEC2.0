package org.example.controller.complaint;

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.Modality;
import org.example.model.Complaint;
import org.example.model.ComplaintHistoryEntry;
import org.example.model.User;
import org.example.model.enums.ComplaintStatus;
import org.example.service.ComplaintService;
import org.example.util.MapDialog;
import org.example.util.NotificationManager;
import org.example.util.UserSession;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class ComplaintDetailsController {

    private static final DateTimeFormatter HISTORY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML private Label categoryLabel;
    @FXML private Label priorityLabel;
    @FXML private Label dateLabel;
    @FXML private Label addressLabel;
    @FXML private Label coordinatesLabel;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<ComplaintStatus> statusBox;
    @FXML private TextArea noteArea;
    @FXML private ListView<String> historyList;
    @FXML private Button saveButton;

    private Complaint complaint;

    @FXML
    public void initialize() {
        statusBox.getItems().setAll(ComplaintStatus.values());

        User user = UserSession.getLoggedUser();
        boolean canManage = user != null && user.getRole().canManageComplaints();
        statusBox.setDisable(!canManage);
        noteArea.setDisable(!canManage);
        saveButton.setVisible(canManage);
        saveButton.setManaged(canManage);
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
        categoryLabel.setText(complaint.getCategory().toString());
        priorityLabel.setText(complaint.getPriority().toString());
        dateLabel.setText(complaint.getDate().toString());
        addressLabel.setText(complaint.getLocation().getAddress());
        coordinatesLabel.setText(String.format(
                "%.6f, %.6f",
                complaint.getLocation().getLatitude(),
                complaint.getLocation().getLongitude()
        ));
        descriptionArea.setText(complaint.getDescription());
        statusBox.setValue(complaint.getStatus());
        loadHistory();
    }

    @FXML
    private void showOnMap() {
        if (complaint != null) {
            MapDialog.showLocation(
                    complaint.getLocation(),
                    categoryLabel.getScene().getWindow()
            );
        }
    }

    @FXML
    private void save() {
        if (complaint == null || statusBox.getValue() == null) {
            return;
        }

        ComplaintStatus newStatus = statusBox.getValue();
        if (newStatus == complaint.getStatus()) {
            close();
            return;
        }

        if (newStatus == ComplaintStatus.CANCELADO && noteArea.getText().isBlank()) {
            Alert alert = new Alert(
                    Alert.AlertType.WARNING,
                    "Informe uma justificativa para cancelar a reclamação."
            );
            alert.setHeaderText(null);
            alert.initOwner(categoryLabel.getScene().getWindow());
            alert.initModality(Modality.WINDOW_MODAL);
            alert.showAndWait();
            return;
        }

        ComplaintService.updateStatus(
                complaint,
                newStatus,
                UserSession.getLoggedUser(),
                noteArea.getText()
        );
        NotificationManager.success("Status da reclamação atualizado.");
        close();
    }

    private void loadHistory() {
        var items = complaint.getHistory().stream()
                .sorted(Comparator.comparing(ComplaintHistoryEntry::getChangedAt).reversed())
                .map(this::formatHistoryEntry)
                .toList();
        historyList.setItems(FXCollections.observableArrayList(items));
    }

    private String formatHistoryEntry(ComplaintHistoryEntry entry) {
        String transition = entry.getPreviousStatus() == null
                ? entry.getNewStatus().toString()
                : entry.getPreviousStatus() + "  →  " + entry.getNewStatus();
        String note = entry.getNote().isBlank() ? "" : "\n" + entry.getNote();
        return HISTORY_DATE_FORMAT.format(entry.getChangedAt())
                + "  •  " + entry.getResponsible()
                + "\n" + transition
                + note;
    }

    @FXML
    private void close() {
        Stage stage = (Stage) categoryLabel.getScene().getWindow();
        stage.close();
    }
}
