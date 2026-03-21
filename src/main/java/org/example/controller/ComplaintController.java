package org.example.controller;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Duration;
import org.example.model.AddressSuggestion;
import org.example.model.enums.ComplaintCategory;
import org.example.util.AddressSearchService;
import org.example.util.GeocodingService;
import org.example.util.ScreenManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.Complaint;
import org.example.service.ComplaintService;

import java.time.LocalDate;

import static org.example.model.enums.ComplaintStatus.*;

public class ComplaintController {

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
    private ComboBox<AddressSuggestion> locationField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Parent root;



    @FXML
    public void initialize() {

        aplicarEmTodos(root);

        locationField.getEditor().textProperty().addListener((obs, oldText, newText) -> {

            if(newText.length() < 3) return;

            var results = AddressSearchService.search(newText);

            locationField.getItems().setAll(results);

            locationField.show();

        });

        if (categoryBox != null) {

            categoryBox.getItems().addAll(ComplaintCategory.values());
        }

        if (complaintsTable != null) {

            categoryColumn.setCellValueFactory(
                    new PropertyValueFactory<>("category")
            );

            locationColumn.setCellValueFactory(
                    new PropertyValueFactory<>("location")
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

            complaintsTable.setItems(
                    FXCollections.observableArrayList(
                            ComplaintService.getAllComplaints()
                    )
            );
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

    private void aplicarEmTodos(Node node) {
        if (node instanceof Button) {
            aplicarEfeito((Button) node);
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                aplicarEmTodos(child);
            }
        }
    }

    private void aplicarEfeito(Button botao) {
        ScaleTransition aumentar = new ScaleTransition(Duration.millis(150), botao);
        aumentar.setToX(1.1);
        aumentar.setToY(1.1);

        ScaleTransition diminuir = new ScaleTransition(Duration.millis(150), botao);
        diminuir.setToX(1.0);
        diminuir.setToY(1.0);

        botao.setOnMouseEntered(e -> aumentar.playFromStart());
        botao.setOnMouseExited(e -> diminuir.playFromStart());
    }
    public void submitComplaint() {

        ComplaintCategory category = categoryBox.getValue();
        AddressSuggestion selected = locationField.getValue();

        double lat = selected.getLat();
        double lon = selected.getLon();

        String location = selected.getDisplayName();
        String description = descriptionArea.getText();

        double[] coords = GeocodingService.getCoordinates(location);

        if(coords != null){

            lat = coords[0];
            lon = coords[1];

            System.out.println("Latitude: " + lat);
            System.out.println("Longitude: " + lon);

        }

        Complaint complaint = new Complaint(category, location, description, PENDENTE);

        ComplaintService.addComplaint(complaint);

    }

    public void goHome() {
        ScreenManager.loadScreen("HomeContent.fxml");
    }

}