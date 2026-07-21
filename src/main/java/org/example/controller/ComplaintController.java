package org.example.controller;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Duration;
import org.example.model.Location;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintStatus;
import org.example.util.MapDialog;
import org.example.util.ScreenManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.Complaint;
import org.example.service.ComplaintService;
import org.example.util.StreetLoader;

import java.time.LocalDate;

import static org.example.model.enums.ComplaintStatus.*;

public class ComplaintController {

    private Location selectedLocation;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<ComplaintCategory> categoryFilter;

    @FXML
    private ComboBox<ComplaintStatus> statusFilter;

    @FXML
    private TableView<Complaint> complaintsTable;

    @FXML
    private TableColumn<Complaint, String> categoryColumn;

    @FXML
    private TableColumn<Complaint, String> locationColumn;

    @FXML
    private TableColumn<Complaint, String> descriptionColumn;

    @FXML
    private TableColumn<Complaint, ComplaintStatus> statusColumn;

    @FXML
    private TableColumn<Complaint, LocalDate> dateColumn;

    @FXML
    private TableColumn<Complaint, Void> actionColumn;

    @FXML
    private ComboBox<ComplaintCategory> categoryBox;

    @FXML
    private TextField addressField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Parent root;

    private ObservableList<Complaint> listaComplaints;

    private PauseTransition debounce = new PauseTransition(Duration.seconds(1));

    @FXML
    public void initialize() {

        ComplaintService.aplicarEmTodos(root);

        if (categoryBox != null) {

            categoryBox.getItems().addAll(ComplaintCategory.values());
        }

        if (complaintsTable != null) {

            categoryColumn.setCellValueFactory(
                    new PropertyValueFactory<>("category")
            );

            locationColumn.setCellValueFactory(cell ->
                    new SimpleStringProperty(cell.getValue().getLocation().getAddress())
            );

            descriptionColumn.setCellValueFactory(
                    new PropertyValueFactory<>("description")
            );

            statusColumn.setCellValueFactory(
                    new PropertyValueFactory<>("status")
            );

            statusColumn.setCellFactory(column -> new TableCell<>() {

                @Override
                protected void updateItem(ComplaintStatus status, boolean empty) {

                    super.updateItem(status, empty);

                    if (empty || status == null) {
                        setGraphic(null);
                        return;
                    }

                    Label badge = new Label();
                    badge.getStyleClass().add("status-badge");

                    switch (status) {

                        case PENDENTE:
                            badge.setText("⏳ Pendente");
                            badge.getStyleClass().add("status-pending");
                            break;

                        case EM_ANALISE:
                            badge.setText("🔎 Em análise");
                            badge.getStyleClass().add("status-analysis");
                            break;

                        case EM_EXECUCAO:
                            badge.setText("🛠 Em execução");
                            badge.getStyleClass().add("status-execution");
                            break;

                        case RESOLVIDO:
                            badge.setText("✅ Resolvido");
                            badge.getStyleClass().add("status-resolved");
                            break;

                        case CANCELADO:
                            badge.setText("❌ Cancelado");
                            badge.getStyleClass().add("status-cancelled");
                            break;
                    }

                    setGraphic(badge);
                }
            });

            dateColumn.setCellValueFactory(
                    new PropertyValueFactory<>("date")
            );

            listaComplaints = FXCollections.observableArrayList(
                    ComplaintService.getAllComplaints()
            );

            FilteredList<Complaint> filteredList =
                    new FilteredList<>(listaComplaints, complaint -> true);

            complaintsTable.setItems(filteredList);

            categoryFilter.getItems().setAll(ComplaintCategory.values());

            statusFilter.getItems().setAll(ComplaintStatus.values());

            searchField.textProperty().addListener((obs, oldValue, newValue) ->
                    applyFilters(filteredList));

            categoryFilter.valueProperty().addListener((obs, oldValue, newValue) ->
                    applyFilters(filteredList));

            statusFilter.valueProperty().addListener((obs, oldValue, newValue) ->
                    applyFilters(filteredList));
        }

        if (actionColumn != null) {

            actionColumn.setCellFactory(column -> new TableCell<>() {

                private final MenuButton menu = new MenuButton("Status");

                @Override
                protected void updateItem(Void item, boolean empty) {

                    super.updateItem(item, empty);

                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                        return;
                    }

                    Complaint complaint = getTableRow().getItem();

                    menu.getItems().clear();

                    for (ComplaintStatus status : ComplaintStatus.values()) {

                        MenuItem menuItem = new MenuItem(status.toString());

                        menuItem.setOnAction(e -> {

                            complaint.setStatus(status);

                            complaintsTable.refresh();

                        });

                        menu.getItems().add(menuItem);
                    }

                    setGraphic(menu);
                }
            });
        }
    }

    @FXML
    private void selectLocation() {

        Location location = MapDialog.show();

        if (location != null) {

            selectedLocation = location;

            addressField.setText(location.getAddress());
        }
    }

    @FXML
    public void submitComplaint() {

        ComplaintCategory category = categoryBox.getValue();

        String description = descriptionArea.getText();

        if (selectedLocation == null) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Selecione uma localização no mapa.");

            alert.showAndWait();

            return;
        }

        Complaint complaint = new Complaint(
                category,
                selectedLocation,
                description,
                PENDENTE
        );

        ComplaintService.addComplaint(complaint);

        if (listaComplaints != null) {
            listaComplaints.add(complaint);
        }

        clearForm();
    }

    private void clearForm() {

        categoryBox.getSelectionModel().clearSelection();

        descriptionArea.clear();

        addressField.clear();

        selectedLocation = null;
    }

    @FXML
    private void clearFilters() {

        searchField.clear();

        categoryFilter.setValue(null);

        statusFilter.setValue(null);

    }

    @FXML
    private void seeGraphs() {
        GraphsController controller = ScreenManager.loadScreen("GraphsView.fxml");
        if (controller != null) {
            controller.setData(listaComplaints);
        }
    }

    private void applyFilters(FilteredList<Complaint> filtered) {

        filtered.setPredicate(complaint -> {

            boolean matchesSearch = true;
            boolean matchesCategory = true;
            boolean matchesStatus = true;

            String text = searchField.getText();

            if (text != null && !text.isBlank()) {

                String search = text.toLowerCase();

                matchesSearch =
                        complaint.getDescription().toLowerCase().contains(search)
                                || complaint.getLocation().getAddress().toLowerCase().contains(search)
                                || complaint.getCategory().toString().toLowerCase().contains(search);

            }

            if (categoryFilter.getValue() != null) {

                matchesCategory =
                        complaint.getCategory() == categoryFilter.getValue();

            }

            if (statusFilter.getValue() != null) {

                matchesStatus =
                        complaint.getStatus() == statusFilter.getValue();

            }

            return matchesSearch
                    && matchesCategory
                    && matchesStatus;

        });
    }

    public void goStart() {
        ScreenManager.loadScreen("Home.fxml");
    }

}