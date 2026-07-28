package org.example.controller.complaint;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.controller.GraphsController;
import org.example.model.Complaint;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintStatus;
import org.example.service.ComplaintService;
import org.example.util.ScreenManager;

import java.time.LocalDate;

public class ComplaintListController {

    @FXML
    private Parent root;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<ComplaintCategory> categoryFilter;

    @FXML
    private ComboBox<ComplaintStatus> statusFilter;

    @FXML
    private TableView<Complaint> complaintsTable;

    @FXML
    private TableColumn<Complaint, ComplaintCategory> categoryColumn;

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

    private ObservableList<Complaint> complaints;

    private FilteredList<Complaint> filteredList;

    @FXML
    public void initialize() {

        ComplaintService.aplicarEmTodos(root);

        configureColumns();

        loadComplaints();

        configureFilters();

        configureActions();
    }

    private void configureColumns() {

        categoryColumn.setCellValueFactory(
                new PropertyValueFactory<>("category")
        );

        locationColumn.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue()
                                .getLocation()
                                .getAddress()
                )
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

        configureStatusBadges();
    }

    private void loadComplaints() {

        complaints = FXCollections.observableArrayList(
                ComplaintService.getAllComplaints()
        );

        filteredList = new FilteredList<>(
                complaints,
                complaint -> true
        );

        complaintsTable.setItems(filteredList);
    }

    private void configureFilters() {

        categoryFilter.getItems().setAll(
                ComplaintCategory.values()
        );

        statusFilter.getItems().setAll(
                ComplaintStatus.values()
        );

        searchField.textProperty().addListener((o,a,b)->applyFilters());

        categoryFilter.valueProperty().addListener((o,a,b)->applyFilters());

        statusFilter.valueProperty().addListener((o,a,b)->applyFilters());
    }

    private void applyFilters() {

        filteredList.setPredicate(complaint -> {

            boolean matchesSearch = true;

            boolean matchesCategory = true;

            boolean matchesStatus = true;

            if (!searchField.getText().isBlank()) {

                String text =
                        searchField.getText().toLowerCase();

                matchesSearch =
                        complaint.getDescription().toLowerCase().contains(text)
                                ||
                                complaint.getLocation().getAddress().toLowerCase().contains(text)
                                ||
                                complaint.getCategory().toString().toLowerCase().contains(text);
            }

            if(categoryFilter.getValue()!=null){

                matchesCategory=
                        complaint.getCategory()==categoryFilter.getValue();
            }

            if(statusFilter.getValue()!=null){

                matchesStatus=
                        complaint.getStatus()==statusFilter.getValue();
            }

            return matchesSearch &&
                    matchesCategory &&
                    matchesStatus;

        });
    }

    private void configureStatusBadges(){

        statusColumn.setCellFactory(column -> new TableCell<>(){

            @Override
            protected void updateItem(ComplaintStatus status, boolean empty){

                super.updateItem(status,empty);

                if(empty || status==null){

                    setGraphic(null);

                    return;
                }

                Label badge=new Label(status.toString());

                badge.getStyleClass().add("status-badge");

                switch(status){

                    case PENDENTE ->
                            badge.getStyleClass().add("status-pending");

                    case EM_ANALISE ->
                            badge.getStyleClass().add("status-analysis");

                    case EM_EXECUCAO ->
                            badge.getStyleClass().add("status-execution");

                    case RESOLVIDO ->
                            badge.getStyleClass().add("status-resolved");

                    case CANCELADO ->
                            badge.getStyleClass().add("status-cancelled");
                }

                setGraphic(badge);

                setText(null);

            }

        });

    }

    private void configureActions(){

        actionColumn.setCellFactory(column -> new TableCell<>(){

            private final MenuButton menu=new MenuButton("Ações");

            @Override
            protected void updateItem(Void item,boolean empty){

                super.updateItem(item,empty);

                if(empty){

                    setGraphic(null);

                    return;
                }

                Complaint complaint=
                        getTableView().getItems().get(getIndex());

                menu.getItems().clear();

                for(ComplaintStatus status:ComplaintStatus.values()){

                    MenuItem itemStatus=
                            new MenuItem(status.toString());

                    itemStatus.setOnAction(e->{

                        complaint.setStatus(status);

                        complaintsTable.refresh();

                    });

                    menu.getItems().add(itemStatus);

                }

                setGraphic(menu);

            }

        });

    }

    @FXML
    private void clearFilters(){

        searchField.clear();

        categoryFilter.setValue(null);

        statusFilter.setValue(null);
    }

    @FXML
    private void seeGraphs(){

        GraphsController controller =
                ScreenManager.loadScreen("GraphsView.fxml");

        if(controller!=null){

            controller.setData(complaints);
        }
    }

}