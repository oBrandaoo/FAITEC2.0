package org.example.controller.complaint;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.service.ComplaintService;
import org.example.service.GeocodingService;
import org.example.util.MapDialog;
import org.example.util.NotificationManager;
import org.example.util.ScreenManager;
import org.example.util.UserSession;

import java.util.concurrent.CompletableFuture;

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
    private Button searchAddressButton;

    @FXML
    private TextArea descriptionArea;

    private boolean updatingAddress;

    @FXML
    public void initialize() {

        ComplaintService.aplicarEmTodos(root);

        categoryBox.getItems().setAll(
                ComplaintCategory.values()
        );
        priorityBox.getItems().setAll(ComplaintPriority.values());
        priorityBox.setValue(ComplaintPriority.MEDIA);

        addressField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!updatingAddress) {
                selectedLocation = null;
            }
        });
    }

    @FXML
    private void searchAddress() {
        String address = addressField.getText().trim();
        if (address.isBlank()) {
            showWarning("Digite um endereço para buscar.");
            return;
        }

        setAddressSearchLoading(true);
        CompletableFuture
                .supplyAsync(() -> GeocodingService.search(address))
                .thenAccept(location -> Platform.runLater(() -> {
                    setAddressSearchLoading(false);
                    if (location == null) {
                        NotificationManager.error(
                                "Endereço não encontrado. Tente informar rua e número."
                        );
                        return;
                    }
                    setSelectedLocation(location);
                    NotificationManager.success("Endereço localizado com sucesso.");
                }));
    }

    @FXML
    private void selectLocation() {

        Location location = MapDialog.show(addressField.getScene().getWindow());

        if (location != null) {
            setSelectedLocation(location);
        }
    }

    private void setSelectedLocation(Location location) {
        selectedLocation = location;
        updatingAddress = true;
        addressField.setText(location.getAddress());
        updatingAddress = false;
    }

    private void setAddressSearchLoading(boolean loading) {
        addressField.setDisable(loading);
        searchAddressButton.setDisable(loading);
        searchAddressButton.setText(loading ? "Buscando..." : "Buscar");
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
                        ? "SYSTEM"
                        : UserSession.getLoggedUser().getId(),
                UserSession.getLoggedUser() == null
                        ? "Sistema"
                        : UserSession.getLoggedUser().getName()
        );

        ComplaintService.addComplaint(complaint);

        clearForm();
        NotificationManager.success("Reclamação registrada com sucesso.");
    }

    private boolean validateForm() {

        if (categoryBox.getValue() == null) {

            showWarning("Selecione uma categoria.");

            return false;
        }

        if (selectedLocation == null) {

            showWarning("Busque o endereço digitado ou selecione um local no mapa.");

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

        alert.setTitle("Atenção");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(addressField.getScene().getWindow());
        alert.initModality(Modality.WINDOW_MODAL);

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

        ScreenManager.loadHomeScreen();
    }
}
