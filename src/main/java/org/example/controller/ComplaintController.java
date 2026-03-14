package org.example.controller;

import javafx.scene.control.cell.PropertyValueFactory;
import org.example.util.ScreenManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.model.Complaint;
import org.example.service.ComplaintService;

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

            complaintsTable.setItems(
                    FXCollections.observableArrayList(
                            ComplaintService.getAllComplaints()
                    )
            );
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
        ScreenManager.loadScreen("Home.fxml");
    }

}