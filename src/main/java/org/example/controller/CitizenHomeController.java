package org.example.controller;

import java.util.Comparator;
import java.util.List;

import org.example.model.Complaint;
import org.example.model.User;
import org.example.model.enums.ComplaintStatus;
import org.example.service.ComplaintService;
import org.example.util.ScreenManager;
import org.example.util.UserSession;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class CitizenHomeController {

    @FXML private Label welcomeLabel;
    @FXML private Label totalLabel;
    @FXML private Label pendingLabel;
    @FXML private Label inProgressLabel;
    @FXML private Label resolvedLabel;
    @FXML private ListView<String> recentList;

    @FXML
    public void initialize() {
        User user = UserSession.getLoggedUser();
        welcomeLabel.setText(user == null ? "Olá!" : "Olá, " + user.getName() + "!");

        List<Complaint> complaints = ComplaintService.getComplaintsFor(user);
        totalLabel.setText(String.valueOf(complaints.size()));
        pendingLabel.setText(String.valueOf(count(complaints, ComplaintStatus.PENDENTE)));
        inProgressLabel.setText(String.valueOf(count(complaints, ComplaintStatus.EM_ANALISE)
            + count(complaints, ComplaintStatus.EM_EXECUCAO)));
        resolvedLabel.setText(String.valueOf(count(complaints, ComplaintStatus.RESOLVIDO)));

        List<String> recent = complaints.stream()
            .sorted(Comparator.comparing(Complaint::getDate).reversed()).limit(5)
            .map(complaint -> complaint.getDate()
                + "  •  " + complaint.getCategory()
                + "  •  " + complaint.getStatus()
                + "\n" + complaint.getLocation().getAddress())
            .toList();
        recentList.setItems(FXCollections.observableArrayList(recent));
        recentList.setPlaceholder(new Label("Você ainda não registrou reclamações."));
    }

    private long count(List<Complaint> complaints, ComplaintStatus status) {
        return complaints.stream()
            .filter(complaint -> complaint.getStatus() == status).count();
    }

    @FXML
    private void newComplaint() {
        ScreenManager.loadScreen("ComplaintForm.fxml");
    }

    @FXML
    private void viewComplaints() {
        ScreenManager.loadScreen("ComplaintList.fxml");
    }
}
