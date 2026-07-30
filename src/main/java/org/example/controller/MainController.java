package org.example.controller;

import org.example.util.ScreenManager;
import org.example.model.User;
import org.example.util.UserSession;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML private Button newComplaintButton;
    @FXML private Button complaintsButton;
    @FXML private Button mapButton;
    @FXML private Label loggedUserLabel;
    @FXML private Label loggedUserRoleLabel;

    @FXML
    public void initialize() {

        ScreenManager.setMainContainer(contentArea);

        configurePermissions();
        ScreenManager.loadScreen("Home.fxml");
    }

    private void configurePermissions() {
        User user = UserSession.getLoggedUser();
        if (user == null) {
            return;
        }

        setAvailable(newComplaintButton, user.getRole().canCreateComplaint());
        setAvailable(mapButton, user.getRole().canViewMap());
        loggedUserLabel.setText(user.getName());
        loggedUserRoleLabel.setText(user.getRole().toString());
    }

    private void setAvailable(Button button, boolean available) {
        button.setVisible(available);
        button.setManaged(available);
    }

    @FXML
    private void goHome() {
        ScreenManager.loadScreen("Home.fxml");
    }

    @FXML
    private void goComplaint() {
        ScreenManager.loadScreen("ComplaintForm.fxml");
    }

    @FXML
    private void goComplaints() {
        ScreenManager.loadScreen("ComplaintList.fxml");
    }

    @FXML
    private void goMap() {
        ScreenManager.loadScreen("map/MapView.fxml");
    }

    @FXML
    private void goSettings() {
        ScreenManager.loadScreen("Settings.fxml");
    }

    @FXML
    private void goAbout() {
        ScreenManager.loadScreen("About.fxml");
    }

}
