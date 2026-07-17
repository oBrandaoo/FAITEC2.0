package org.example.controller;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Duration;
import org.example.model.Location;
import org.example.model.enums.ComplaintCategory;
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
    private TableView<Complaint> complaintsTable;

    @FXML
    private TableColumn<Complaint, String> categoryColumn;

    @FXML
    private TableColumn<Complaint, String> locationColumn;

    @FXML
    private TableColumn<Complaint, String> descriptionColumn;

    @FXML
    private TableColumn<Complaint, String> statusColumn;

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

            dateColumn.setCellValueFactory(
                    new PropertyValueFactory<>("date")
            );

            listaComplaints = FXCollections.observableArrayList(
                    ComplaintService.getAllComplaints()
            );

            complaintsTable.setItems(listaComplaints);
        }

        if (actionColumn != null) {

            actionColumn.setCellFactory(new Callback<>() {

                @Override
                public TableCell<Complaint, Void> call(final TableColumn<Complaint, Void> param) {

                    return new TableCell<>() {

                        private final Button btn = new Button("Resolver");

                        {
                            btn.setOnAction(event -> {

                                Complaint complaint = getTableView().getItems().get(getIndex());

                                complaint.setStatus(RESOLVIDO);

                                complaintsTable.refresh();
                            });
                        }

                        @Override
                        protected void updateItem(Void item, boolean empty) {
                            super.updateItem(item, empty);

                            if (empty) {
                                setGraphic(null);
                            } else {
                                setGraphic(btn);
                            }
                        }
                    };
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

        clearForm();
    }

    private void clearForm() {

        categoryBox.getSelectionModel().clearSelection();

        descriptionArea.clear();

        addressField.clear();

        selectedLocation = null;
    }

    @FXML
    private void seeGraphs() {
        GraphsController controller = ScreenManager.loadScreen("GraphsView.fxml");
        if (controller != null) {
            controller.setData(listaComplaints);
        }
    }

    public void goStart() {
        ScreenManager.loadScreen("Home.fxml");
    }

}