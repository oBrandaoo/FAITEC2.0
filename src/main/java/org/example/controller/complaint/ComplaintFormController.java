package org.example.controller.complaint;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.service.ComplaintService;
import org.example.util.MapDialog;
import org.example.util.ScreenManager;
import org.example.util.UserSession;

import static org.example.model.enums.ComplaintStatus.PENDENTE;

public class ComplaintFormController {

    private Location selectedLocation;

    @FXML
    private Parent root;

    @FXML
    private ComboBox<ComplaintCategory> categoryBox;

    @FXML
    private ComboBox<ComplaintPriority> priorityBox;

    @FXML
    private TextField addressField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    public void initialize() {

        ComplaintService.aplicarEmTodos(root);

        categoryBox.getItems().setAll(
                ComplaintCategory.values()
        );
        priorityBox.getItems().setAll(ComplaintPriority.values());
        priorityBox.setValue(ComplaintPriority.MEDIA);
    }

    @FXML
    private void selectLocation() {

        Location location = MapDialog.show();

        if (location != null) {

            selectedLocation = location;

            addressField.setText(
                    location.getAddress()
            );
        }
    }

    @FXML
    private void submitComplaint() {

        if (!validateForm()) {
            return;
        }

        Complaint complaint = new Complaint(
                categoryBox.getValue(),
                selectedLocation,
                descriptionArea.getText().trim(),
                PENDENTE,
                priorityBox.getValue(),
                java.time.LocalDate.now(),
                UserSession.getLoggedUser() == null
                        ? "Sistema"
                        : UserSession.getLoggedUser().getName()
        );

        ComplaintService.addComplaint(complaint);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText("Reclamação registrada com sucesso.");
        alert.showAndWait();

        clearForm();
    }

    private boolean validateForm() {

        if (categoryBox.getValue() == null) {

            showWarning("Selecione uma categoria.");

            return false;
        }

        if (selectedLocation == null) {

            showWarning("Selecione um local no mapa.");

            return false;
        }

        if (priorityBox.getValue() == null) {
            showWarning("Selecione uma prioridade.");
            return false;
        }

        if (descriptionArea.getText().isBlank()) {

            showWarning("Informe uma descrição.");

            return false;
        }

        return true;
    }

    private void showWarning(String message) {

        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.showAndWait();
    }

    private void clearForm() {

        categoryBox.getSelectionModel().clearSelection();
        priorityBox.setValue(ComplaintPriority.MEDIA);

        descriptionArea.clear();

        addressField.clear();

        selectedLocation = null;
    }

    @FXML
    private void goStart() {

        ScreenManager.loadScreen("Home.fxml");
    }
}
