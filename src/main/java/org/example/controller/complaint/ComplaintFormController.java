package org.example.controller.complaint;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintSubcategory;
import org.example.service.ComplaintService;
import org.example.service.GeocodingService;
import org.example.util.MapDialog;
import org.example.util.NotificationManager;
import org.example.util.ScreenManager;
import org.example.util.UserSession;

import java.util.concurrent.CompletableFuture;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.example.model.enums.ComplaintStatus.PENDENTE;

public class ComplaintFormController {

    private static final int MAX_ATTACHMENTS = 4;
    private static final long MAX_ATTACHMENT_SIZE = 5L * 1024 * 1024;

    private Location selectedLocation;
    private final List<File> selectedAttachments = new ArrayList<>();
    private Complaint editingComplaint;
    private boolean editingFinished;

    @FXML
    private Parent root;

    @FXML
    private ComboBox<ComplaintCategory> categoryBox;

    @FXML
    private ComboBox<ComplaintSubcategory> subcategoryBox;

    @FXML
    private ComboBox<ComplaintPriority> priorityBox;

    @FXML
    private TextField addressField;

    @FXML
    private Button searchAddressButton;

    @FXML
    private FlowPane attachmentPreview;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Label formTitleLabel;

    @FXML
    private Label formSubtitleLabel;

    @FXML
    private Button submitButton;

    private boolean updatingAddress;

    @FXML
    public void initialize() {

        ComplaintService.aplicarEmTodos(root);

        categoryBox.getItems().setAll(
                ComplaintCategory.values()
        );
        subcategoryBox.setDisable(true);
        categoryBox.valueProperty().addListener((observable, oldCategory, newCategory) ->
                updateSubcategories(newCategory)
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

        if (editingComplaint != null) {
            saveEditingComplaint();
            return;
        }

        Complaint complaint = new Complaint(
                categoryBox.getValue(),
                subcategoryBox.getValue(),
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

        selectedAttachments.forEach(file ->
                complaint.addAttachment(file.getAbsolutePath())
        );

        clearForm();
        NotificationManager.success("Reclamação registrada com sucesso.");
    }

    public void setEditingComplaint(Complaint complaint) {
        editingComplaint = complaint;
        formTitleLabel.setText("Editar reclamação");
        formSubtitleLabel.setText("Atualize os dados enquanto a solicitação estiver pendente.");
        submitButton.setText("Salvar alterações");

        categoryBox.setValue(complaint.getCategory());
        subcategoryBox.setValue(complaint.getSubcategory());
        priorityBox.setValue(complaint.getPriority());
        descriptionArea.setText(complaint.getDescription());
        setSelectedLocation(complaint.getLocation());

        selectedAttachments.clear();
        complaint.getAttachmentPaths().stream()
                .map(File::new)
                .filter(File::isFile)
                .forEach(selectedAttachments::add);
        refreshAttachmentPreview();
    }

    private void saveEditingComplaint() {
        String responsible = UserSession.getLoggedUser() == null
                ? "Sistema"
                : UserSession.getLoggedUser().getName();

        boolean detailsChanged = editingComplaint.updateDetails(
                categoryBox.getValue(),
                subcategoryBox.getValue(),
                selectedLocation,
                descriptionArea.getText().trim(),
                priorityBox.getValue(),
                responsible
        );

        List<String> paths = selectedAttachments.stream()
                .map(File::getAbsolutePath)
                .toList();
        boolean attachmentsChanged = editingComplaint.replaceAttachments(paths);
        if (attachmentsChanged) {
            editingComplaint.addHistoryNote(responsible, "Fotos da reclamação atualizadas.");
        }

        if (!detailsChanged && !attachmentsChanged) {
            NotificationManager.info("Nenhuma alteração foi realizada.");
        } else {
            NotificationManager.success("Reclamação atualizada com sucesso.");
        }
        editingFinished = true;
        closeFormWindow();
    }

    @FXML
    private void chooseAttachments() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecionar fotos");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagens (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg")
        );

        List<File> files = chooser.showOpenMultipleDialog(addressField.getScene().getWindow());
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (selectedAttachments.size() >= MAX_ATTACHMENTS) {
                NotificationManager.warning("É possível anexar no máximo 4 fotos.");
                break;
            }
            if (file.length() > MAX_ATTACHMENT_SIZE) {
                NotificationManager.warning(file.getName() + " ultrapassa o limite de 5 MB.");
                continue;
            }
            boolean alreadySelected = selectedAttachments.stream()
                    .anyMatch(selected -> selected.getAbsolutePath().equals(file.getAbsolutePath()));
            if (!alreadySelected) {
                selectedAttachments.add(file);
            }
        }
        refreshAttachmentPreview();
    }

    private void refreshAttachmentPreview() {
        attachmentPreview.getChildren().clear();

        for (File file : selectedAttachments) {
            ImageView imageView = new ImageView(
                    new Image(file.toURI().toString(), 82, 68, true, true)
            );
            imageView.setFitWidth(82);
            imageView.setFitHeight(68);
            imageView.setPreserveRatio(true);

            Button removeButton = new Button("×");
            removeButton.getStyleClass().add("attachment-remove");
            StackPane.setAlignment(removeButton, Pos.TOP_RIGHT);
            removeButton.setOnAction(event -> {
                selectedAttachments.remove(file);
                refreshAttachmentPreview();
            });

            StackPane imageBox = new StackPane(imageView, removeButton);
            imageBox.getStyleClass().add("attachment-image-box");

            String displayName = file.getName().length() > 15
                    ? file.getName().substring(0, 12) + "..."
                    : file.getName();
            Label nameLabel = new Label(displayName);
            nameLabel.getStyleClass().add("attachment-name");

            VBox item = new VBox(4, imageBox, nameLabel);
            item.setAlignment(Pos.CENTER);
            attachmentPreview.getChildren().add(item);
        }
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
        subcategoryBox.getSelectionModel().clearSelection();
        priorityBox.setValue(ComplaintPriority.MEDIA);

        descriptionArea.clear();

        addressField.clear();

        selectedLocation = null;
        selectedAttachments.clear();
        attachmentPreview.getChildren().clear();
    }

    private void updateSubcategories(ComplaintCategory category) {
        subcategoryBox.getItems().setAll(ComplaintSubcategory.forCategory(category));
        subcategoryBox.getSelectionModel().clearSelection();
        subcategoryBox.setDisable(category == null);
    }

    @FXML
    private void cancelForm() {
        if (editingComplaint != null) {
            if (confirmDiscardChanges()) {
                editingFinished = true;
                closeFormWindow();
            }
        } else {
            ScreenManager.loadHomeScreen();
        }
    }

    public boolean confirmDiscardChanges() {
        if (editingComplaint == null || editingFinished) {
            return true;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Descartar alterações");
        alert.setHeaderText("Deseja fechar sem salvar?");
        alert.setContentText("As alterações feitas no formulário serão perdidas.");
        alert.initOwner(addressField.getScene().getWindow());
        alert.initModality(Modality.WINDOW_MODAL);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void closeFormWindow() {
        Stage stage = (Stage) addressField.getScene().getWindow();
        stage.close();
    }
}
