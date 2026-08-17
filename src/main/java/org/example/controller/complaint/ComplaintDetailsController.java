package org.example.controller.complaint;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import org.example.model.Complaint;
import org.example.model.ComplaintHistoryEntry;
import org.example.model.User;
import org.example.model.enums.ComplaintStatus;
import org.example.model.enums.UserRole;
import org.example.service.ComplaintService;
import org.example.util.MapDialog;
import org.example.util.NotificationManager;
import org.example.util.UserSession;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ComplaintDetailsController {

    private static final DateTimeFormatter HISTORY_DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private Label categoryLabel;
    @FXML private Label subcategoryLabel;
    @FXML private Label priorityLabel;
    @FXML private Label dateLabel;
    @FXML private Label addressLabel;
    @FXML private Label coordinatesLabel;
    @FXML private Label trackingStatusLabel;
    @FXML private Label trackingDescriptionLabel;
    @FXML private Label lastUpdateLabel;
    @FXML private Label otherComplaintsTitleLabel;
    @FXML private ProgressBar trackingProgress;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<ComplaintStatus> statusBox;
    @FXML private TextArea noteArea;
    @FXML private ListView<String> historyList;
    @FXML private ListView<Complaint> otherComplaintsList;
    @FXML private FlowPane attachmentGallery;
    @FXML private Label noAttachmentsLabel;
    @FXML private Button saveButton;
    @FXML private Button editButton;
    @FXML private Button cancelComplaintButton;
    @FXML private VBox managementBox;

    private Complaint complaint;

    @FXML
    public void initialize() {
        statusBox.getItems().setAll(ComplaintStatus.values());
        configurePermissions();
        configureComplaintNavigation();
    }

    private void configurePermissions() {
        User user = UserSession.getLoggedUser();
        boolean canManage = user != null && user.getRole().canManageComplaints();
        statusBox.setDisable(!canManage);
        noteArea.setDisable(!canManage);
        managementBox.setVisible(canManage);
        managementBox.setManaged(canManage);
        saveButton.setVisible(canManage);
        saveButton.setManaged(canManage);
        editButton.setVisible(false);
        editButton.setManaged(false);
        cancelComplaintButton.setVisible(false);
        cancelComplaintButton.setManaged(false);

        otherComplaintsTitleLabel.setText(canManage
                ? "Problemas registrados"
                : "Problemas da cidade");
    }

    private void configureComplaintNavigation() {
        otherComplaintsList.setCellFactory(list -> new ListCell<>() {
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
            protected void updateItem(Complaint item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                String subcategory = item.getSubcategory() == null
                        ? "Sem subcategoria"
                        : item.getSubcategory().toString();
                category.setText(item.getCategory().toString());
                metadata.setText(item.getStatus() + "  •  " + DATE_FORMAT.format(item.getDate())
                        + "\n" + subcategory);
                address.setText(item.getLocation().getAddress());
                setAccessibleText(item.getCategory() + ", " + item.getStatus()
                        + ", " + item.getLocation().getAddress());
                setText(null);
                setGraphic(content);
            }
        });

        otherComplaintsList.getSelectionModel().selectedItemProperty().addListener(
                (observable, previous, selected) -> {
                    if (selected != null && selected != complaint) {
                        displayComplaint(selected);
                    }
                }
        );
    }

    public void setComplaint(Complaint selectedComplaint) {
        var visibleComplaints = ComplaintService
                .getTrackableComplaints(UserSession.getLoggedUser())
                .stream()
                .sorted(Comparator.comparing(Complaint::getDate).reversed())
                .toList();

        otherComplaintsList.setItems(FXCollections.observableArrayList(visibleComplaints));
        displayComplaint(selectedComplaint);
        otherComplaintsList.getSelectionModel().select(selectedComplaint);
    }

    private void displayComplaint(Complaint selectedComplaint) {
        complaint = selectedComplaint;
        categoryLabel.setText(complaint.getCategory().toString());
        subcategoryLabel.setText(complaint.getSubcategory() == null
                ? "Não informada"
                : complaint.getSubcategory().toString());
        priorityLabel.setText(complaint.getPriority().toString());
        dateLabel.setText(DATE_FORMAT.format(complaint.getDate()));
        addressLabel.setText(complaint.getLocation().getAddress());
        coordinatesLabel.setText(String.format(
                "%.6f, %.6f",
                complaint.getLocation().getLatitude(),
                complaint.getLocation().getLongitude()
        ));
        descriptionArea.setText(complaint.getDescription());
        statusBox.setValue(complaint.getStatus());
        noteArea.clear();
        updateTrackingSummary();
        loadAttachments();
        loadHistory();
        configureCitizenActions();
    }

    private void updateTrackingSummary() {
        ComplaintStatus status = complaint.getStatus();
        trackingStatusLabel.setText(status.toString());
        trackingDescriptionLabel.setText(status.getTrackingDescription());
        trackingProgress.setProgress(status.getTrackingProgress());

        trackingStatusLabel.getStyleClass().removeIf(style ->
                style.startsWith("tracking-status-")
        );
        trackingStatusLabel.getStyleClass().add(
                "tracking-status-" + status.name().toLowerCase()
        );

        LocalDateTime lastUpdate = complaint.getHistory().stream()
                .map(ComplaintHistoryEntry::getChangedAt)
                .max(LocalDateTime::compareTo)
                .orElse(complaint.getDate().atStartOfDay());
        lastUpdateLabel.setText("Última atualização: " + HISTORY_DATE_FORMAT.format(lastUpdate));
    }

    @FXML
    private void showOnMap() {
        if (complaint != null) {
            MapDialog.showLocation(complaint.getLocation(), categoryLabel.getScene().getWindow());
        }
    }

    private void configureCitizenActions() {
        boolean available = canCitizenModify();
        editButton.setVisible(available);
        editButton.setManaged(available);
        cancelComplaintButton.setVisible(available);
        cancelComplaintButton.setManaged(available);
    }

    private boolean canCitizenModify() {
        User user = UserSession.getLoggedUser();
        return complaint != null
                && user != null
                && user.getRole() == UserRole.CIDADAO
                && user.getId().equals(complaint.getCreatorId())
                && complaint.getStatus() == ComplaintStatus.PENDENTE;
    }

    @FXML
    private void editComplaint() {
        if (!canCitizenModify()) {
            NotificationManager.warning("Esta reclamação não pode mais ser editada.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ComplaintForm.fxml"));
            Parent view = loader.load();
            ComplaintFormController controller = loader.getController();
            controller.setEditingComplaint(complaint);

            Stage dialog = new Stage();
            dialog.initOwner(categoryLabel.getScene().getWindow());
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setTitle("Editar reclamação");
            dialog.setScene(new Scene(view, 620, 820));
            dialog.setOnCloseRequest(event -> {
                if (!controller.confirmDiscardChanges()) {
                    event.consume();
                }
            });
            dialog.showAndWait();

            displayComplaint(complaint);
            otherComplaintsList.refresh();
        } catch (IOException exception) {
            NotificationManager.error("Não foi possível abrir a edição da reclamação.");
            exception.printStackTrace();
        }
    }

    @FXML
    private void cancelComplaint() {
        if (!canCitizenModify()) {
            NotificationManager.warning("Esta reclamação não pode mais ser cancelada.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancelar reclamação");
        dialog.setHeaderText("Informe por que deseja cancelar esta reclamação.");
        dialog.setContentText("Justificativa:");
        dialog.initOwner(categoryLabel.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);

        var result = dialog.showAndWait();
        if (result.isEmpty()) {
            return;
        }
        if (result.get().isBlank()) {
            NotificationManager.warning("A justificativa é obrigatória.");
            return;
        }

        ComplaintService.updateStatus(
                complaint,
                ComplaintStatus.CANCELADO,
                UserSession.getLoggedUser(),
                result.get()
        );
        refreshCurrentComplaint();
        NotificationManager.success("Reclamação cancelada.");
    }

    @FXML
    private void save() {
        if (complaint == null || statusBox.getValue() == null) {
            return;
        }

        ComplaintStatus newStatus = statusBox.getValue();
        if (newStatus == complaint.getStatus()) {
            NotificationManager.info("O status selecionado já está aplicado.");
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
        refreshCurrentComplaint();
        NotificationManager.success("Status da reclamação atualizado.");
    }

    private void refreshCurrentComplaint() {
        displayComplaint(complaint);
        otherComplaintsList.refresh();
    }

    private void loadHistory() {
        var items = complaint.getHistory().stream()
                .sorted(Comparator.comparing(ComplaintHistoryEntry::getChangedAt).reversed())
                .map(this::formatHistoryEntry)
                .toList();
        historyList.setItems(FXCollections.observableArrayList(items));
    }

    private void loadAttachments() {
        attachmentGallery.getChildren().clear();

        for (String path : complaint.getAttachmentPaths()) {
            File file = new File(path);
            if (!file.isFile()) {
                continue;
            }

            ImageView thumbnail = new ImageView(
                    new Image(file.toURI().toString(), 112, 86, true, true)
            );
            thumbnail.setFitWidth(112);
            thumbnail.setFitHeight(86);
            thumbnail.setPreserveRatio(true);
            thumbnail.getStyleClass().add("attachment-thumbnail");
            thumbnail.setOnMouseClicked(event -> showAttachment(file));
            attachmentGallery.getChildren().add(thumbnail);
        }

        boolean empty = attachmentGallery.getChildren().isEmpty();
        noAttachmentsLabel.setVisible(empty);
        noAttachmentsLabel.setManaged(empty);
    }

    private void showAttachment(File file) {
        ImageView imageView = new ImageView(new Image(file.toURI().toString()));
        imageView.setFitWidth(860);
        imageView.setFitHeight(600);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        StackPane root = new StackPane(imageView);
        root.getStyleClass().add("image-viewer");
        Scene scene = new Scene(root, 900, 640);
        scene.getStylesheets().add(
                getClass().getResource("/css/complaint.css").toExternalForm()
        );

        Stage dialog = new Stage();
        dialog.initOwner(categoryLabel.getScene().getWindow());
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle(file.getName());
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private String formatHistoryEntry(ComplaintHistoryEntry entry) {
        String transition = entry.getPreviousStatus() == null
                ? entry.getNewStatus().toString()
                : entry.getPreviousStatus() == entry.getNewStatus()
                        ? "Atualização da reclamação"
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
