package org.example.controller.complaint;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import org.example.model.Complaint;
import org.example.model.User;
import org.example.model.enums.ComplaintStatus;
import org.example.util.MapDialog;
import org.example.util.UserSession;

public class ComplaintDetailsController {

    @FXML private Label categoryLabel;
    @FXML private Label priorityLabel;
    @FXML private Label dateLabel;
    @FXML private Label addressLabel;
    @FXML private Label coordinatesLabel;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<ComplaintStatus> statusBox;
    @FXML private Button saveButton;

    private Complaint complaint;

    @FXML
    public void initialize() {
        statusBox.getItems().setAll(ComplaintStatus.values());

        User user = UserSession.getLoggedUser();
        boolean canManage = user != null && user.getRole().canManageComplaints();
        statusBox.setDisable(!canManage);
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
    }

    @FXML
    private void showOnMap() {
        if (complaint != null) {
            MapDialog.showLocation(complaint.getLocation());
        }
    }

    @FXML
    private void save() {
        if (complaint != null && statusBox.getValue() != null) {
            complaint.setStatus(statusBox.getValue());
        }
        close();
    }

    @FXML
    private void close() {
        Stage stage = (Stage) categoryLabel.getScene().getWindow();
        stage.close();
    }
}
