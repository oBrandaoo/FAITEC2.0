package org.example.controller.complaint;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.controller.GraphsController;
import org.example.model.Complaint;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.example.service.ComplaintService;
import org.example.util.ScreenManager;
import org.example.model.User;
import org.example.util.UserSession;

import java.time.LocalDate;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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
    private ComboBox<ComplaintPriority> priorityFilter;

    @FXML
    private TableView<Complaint> complaintsTable;

    @FXML
    private Button detailsButton;

    @FXML
    private TableColumn<Complaint, ComplaintCategory> categoryColumn;

    @FXML
    private TableColumn<Complaint, String> locationColumn;

    @FXML
    private TableColumn<Complaint, String> descriptionColumn;

    @FXML
    private TableColumn<Complaint, ComplaintStatus> statusColumn;

    @FXML
    private TableColumn<Complaint, ComplaintPriority> priorityColumn;

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
        configurePermissions();

        loadComplaints();

        configureFilters();

        configureActions();
        configureDetails();
    }

    private void configureDetails() {
        detailsButton.disableProperty().bind(
                complaintsTable.getSelectionModel().selectedItemProperty().isNull()
        );

        complaintsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2
                    && complaintsTable.getSelectionModel().getSelectedItem() != null) {
                showDetails();
            }
        });
    }

    private void configurePermissions() {
        User user = UserSession.getLoggedUser();
        boolean canManage = user != null && user.getRole().canManageComplaints();
        actionColumn.setVisible(canManage);
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

        priorityColumn.setCellValueFactory(
                new PropertyValueFactory<>("priority")
        );

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("date")
        );

        configureStatusBadges();
        configurePriorityBadges();
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

        priorityFilter.getItems().setAll(
                ComplaintPriority.values()
        );

        searchField.textProperty().addListener((o,a,b)->applyFilters());

        categoryFilter.valueProperty().addListener((o,a,b)->applyFilters());

        statusFilter.valueProperty().addListener((o,a,b)->applyFilters());
        priorityFilter.valueProperty().addListener((o,a,b)->applyFilters());
    }

    private void applyFilters() {

        filteredList.setPredicate(complaint -> {

            boolean matchesSearch = true;

            boolean matchesCategory = true;

            boolean matchesStatus = true;
            boolean matchesPriority = true;

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

            if (priorityFilter.getValue() != null) {
                matchesPriority = complaint.getPriority() == priorityFilter.getValue();
            }

            return matchesSearch &&
                    matchesCategory &&
                    matchesStatus &&
                    matchesPriority;

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

    private void configurePriorityBadges() {
        priorityColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(ComplaintPriority priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Label badge = new Label(priority.toString());
                badge.getStyleClass().addAll(
                        "priority-badge",
                        "priority-" + priority.name().toLowerCase()
                );
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

                        String note = "";
                        if (status == ComplaintStatus.CANCELADO
                                && complaint.getStatus() != ComplaintStatus.CANCELADO) {
                            TextInputDialog dialog = new TextInputDialog();
                            dialog.setTitle("Cancelar reclamação");
                            dialog.setHeaderText("Informe a justificativa do cancelamento.");
                            dialog.setContentText("Justificativa:");
                            dialog.initOwner(complaintsTable.getScene().getWindow());

                            var result = dialog.showAndWait();
                            if (result.isEmpty() || result.get().isBlank()) {
                                return;
                            }
                            note = result.get();
                        }

                        ComplaintService.updateStatus(
                                complaint,
                                status,
                                UserSession.getLoggedUser(),
                                note
                        );

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
        priorityFilter.setValue(null);
    }

    @FXML
    private void showDetails() {
        Complaint selected = complaintsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/ComplaintDetails.fxml")
            );
            Parent view = loader.load();

            ComplaintDetailsController controller = loader.getController();
            controller.setComplaint(selected);

            Stage dialog = new Stage();
            dialog.initOwner(complaintsTable.getScene().getWindow());
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setTitle("Detalhes da reclamação");
            dialog.setResizable(false);
            dialog.setScene(new Scene(view));
            dialog.showAndWait();

            complaintsTable.refresh();
            applyFilters();
        } catch (IOException exception) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível abrir os detalhes da reclamação."
            );
            exception.printStackTrace();
        }
    }

    @FXML
    private void exportCsv() {
        if (filteredList == null || filteredList.isEmpty()) {
            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Exportação",
                    "Não há reclamações para exportar."
            );
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exportar reclamações");
        fileChooser.setInitialFileName("reclamacoes-" + LocalDate.now() + ".csv");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivo CSV (*.csv)", "*.csv")
        );

        File selectedFile = fileChooser.showSaveDialog(complaintsTable.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }

        File destination = ensureCsvExtension(selectedFile);
        try {
            writeCsv(destination);
            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Exportação concluída",
                    filteredList.size() + " reclamação(ões) exportada(s) com sucesso."
            );
        } catch (IOException exception) {
            showAlert(
                    Alert.AlertType.ERROR,
                    "Erro na exportação",
                    "Não foi possível salvar o arquivo:\n" + exception.getMessage()
            );
        }
    }

    private void writeCsv(File destination) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(
                destination.toPath(),
                StandardCharsets.UTF_8
        )) {
            // BOM facilita a identificação de UTF-8 pelo Excel.
            writer.write('\uFEFF');
            writer.write("Categoria;Prioridade;Endereço;Descrição;Status;Data");
            writer.newLine();

            for (Complaint complaint : filteredList) {
                writer.write(String.join(";",
                        csvValue(complaint.getCategory().toString()),
                        csvValue(complaint.getPriority().toString()),
                        csvValue(complaint.getLocation().getAddress()),
                        csvValue(complaint.getDescription()),
                        csvValue(complaint.getStatus().toString()),
                        csvValue(complaint.getDate().toString())
                ));
                writer.newLine();
            }
        }
    }

    private String csvValue(String value) {
        String safeValue = value == null ? "" : value;
        return "\"" + safeValue.replace("\"", "\"\"") + "\"";
    }

    private File ensureCsvExtension(File file) {
        if (file.getName().toLowerCase().endsWith(".csv")) {
            return file;
        }
        return new File(file.getParentFile(), file.getName() + ".csv");
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(complaintsTable.getScene().getWindow());
        alert.showAndWait();
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
