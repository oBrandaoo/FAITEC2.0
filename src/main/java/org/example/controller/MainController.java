package org.example.controller;

import org.example.model.User;
import org.example.util.AccessibilityManager;
import org.example.util.NotificationManager;
import org.example.util.ScreenManager;
import org.example.util.UserSession;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private BorderPane mainRoot;

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox toastLayer;

    @FXML private Button newComplaintButton;
    @FXML private Button complaintsButton;
    @FXML private Button mapButton;
    @FXML private Label loggedUserLabel;
    @FXML private Label loggedUserRoleLabel;

    @FXML
    public void initialize() {
        ScreenManager.setMainContainer(contentArea);
        NotificationManager.setContainer(toastLayer);
        AccessibilityManager.setApplicationRoot(mainRoot);

        Platform.runLater(() -> mainRoot.getScene().addEventFilter(
            KeyEvent.KEY_PRESSED, this::handleShortcut));

        configurePermissions();
        ScreenManager.loadHomeScreen();
    }

    private void handleShortcut(KeyEvent event) {
        if (!event.isAltDown()) {
            return;
        }

        KeyCode code = event.getCode();
        if (code == KeyCode.DIGIT1 || code == KeyCode.NUMPAD1) {
            goHome();
        } else if ((code == KeyCode.DIGIT2 || code == KeyCode.NUMPAD2)
            && newComplaintButton.isVisible()) {
            goComplaint();
        } else if (code == KeyCode.DIGIT3 || code == KeyCode.NUMPAD3) {
            goComplaints();
        } else if ((code == KeyCode.DIGIT4 || code == KeyCode.NUMPAD4)
            && mapButton.isVisible()) {
            goMap();
        } else if (code == KeyCode.DIGIT5 || code == KeyCode.NUMPAD5) {
            goSettings();
        } else if (code == KeyCode.DIGIT0 || code == KeyCode.NUMPAD0) {
            goAbout();
        } else {
            return;
        }
        event.consume();
    }

    private void configurePermissions() {
        User user = UserSession.getLoggedUser();
        if (user == null) {
            return;
        }

        setAvailable(newComplaintButton, user.getRole().canCreateComplaint());
        setAvailable(mapButton, user.getRole().canViewMap());
        if (!user.getRole().canManageComplaints()) {
            complaintsButton.setText("📋   Minhas reclamações");
        }
        loggedUserLabel.setText(user.getName());
        loggedUserRoleLabel.setText(user.getRole().toString());
    }

    private void setAvailable(Button button, boolean available) {
        button.setVisible(available);
        button.setManaged(available);
    }

    @FXML
    private void goHome() {
        ScreenManager.loadHomeScreen();
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
