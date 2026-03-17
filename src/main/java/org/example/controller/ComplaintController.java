package org.example.controller;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.example.util.ScreenManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.Complaint;
import org.example.service.ComplaintService;

import java.time.LocalDate;

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
    private ComboBox<String> categoryBox;

    @FXML
    private TextField locationField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    public void initialize() {

        if (categoryBox != null) {

            categoryBox.getItems().addAll(
                    "Buraco na rua",
                    "Iluminação pública",
                    "Lixo acumulado",
                    "Esgoto",
                    "Segurança"
            );
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

                                complaint.setStatus("RESOLVIDA");

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

    public void submitComplaint() {

        String category = categoryBox.getValue();
        String location = locationField.getText();
        String description = descriptionArea.getText();

        Complaint complaint = new Complaint(category, location, description);

        ComplaintService.addComplaint(complaint);

        System.out.println("Reclamação registrada!");
    }

    public void goHome() {
        ScreenManager.loadScreen("HomeContent.fxml");
    }

}