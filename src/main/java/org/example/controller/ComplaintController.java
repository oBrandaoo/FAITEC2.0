package org.example.controller;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Duration;
import org.example.model.enums.ComplaintCategory;
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
    private ComboBox<String> locationField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private Parent root;

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
    
    public void submitComplaint() {

        ComplaintCategory category = categoryBox.getValue();

        String typedText = locationField.getEditor().getText();

        String location = null;

        String description = descriptionArea.getText();

        Complaint complaint = new Complaint(category, location, description, PENDENTE);

        ComplaintService.addComplaint(complaint);
    }

    public void goHome() {
        ScreenManager.loadScreen("HomeContent.fxml");
    }

}