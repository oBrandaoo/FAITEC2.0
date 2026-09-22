package org.example.controller;

import java.util.List;
import java.util.Optional;

import org.example.model.User;
import org.example.model.enums.UserRole;
import org.example.util.AccessibilityManager;
import org.example.util.NotificationManager;
import org.example.util.ScreenManager;
import org.example.util.UserSession;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private BorderPane mainRoot;

    @FXML
    private StackPane contentArea;

    @FXML
    private VBox toastLayer;

    @FXML private Button homeButton;
    @FXML private Button newComplaintButton;
    @FXML private Button complaintsButton;
    @FXML private Button analyticsButton;
    @FXML private Button mapButton;
    @FXML private Button trackingButton;
    @FXML private Button settingsButton;
    @FXML private Button aboutButton;
    @FXML private Button switchUserButton;
    @FXML private Label loggedUserLabel;
    @FXML private Label loggedUserRoleLabel;

    @FXML
    public void initialize() {
        ScreenManager.setMainContainer(contentArea);
        ScreenManager.setScreenChangeListener(this::updateActiveNavigation);
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
        } else if ((code == KeyCode.DIGIT3 || code == KeyCode.NUMPAD3)
            && complaintsButton.isVisible()) {
            goComplaints();
        } else if ((code == KeyCode.DIGIT4 || code == KeyCode.NUMPAD4)
            && mapButton.isVisible()) {
            goMap();
        } else if (code == KeyCode.DIGIT5 || code == KeyCode.NUMPAD5) {
            goTracking();
        } else if (code == KeyCode.DIGIT6 || code == KeyCode.NUMPAD6) {
            goSettings();
        } else if ((code == KeyCode.DIGIT7 || code == KeyCode.NUMPAD7)
            && analyticsButton.isVisible()) {
            goAnalytics();
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
        setAvailable(complaintsButton, user.getRole().canManageComplaints());
        setAvailable(mapButton, user.getRole().canViewMap());
        setAvailable(trackingButton, user.getRole().canViewMap());
        setAvailable(analyticsButton, user.getRole() == UserRole.ADMINISTRADOR);
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
    private void goAnalytics() {
        ScreenManager.loadScreen("Analytics.fxml");
    }

    @FXML
    private void goMap() {
        ScreenManager.loadScreen("map/MapView.fxml");
    }

    @FXML
    private void goTracking() {
        ScreenManager.loadScreen("ComplaintTracking.fxml");
    }

    @FXML
    private void goSettings() {
        ScreenManager.loadScreen("Settings.fxml");
    }

    @FXML
    private void goAbout() {
        ScreenManager.loadScreen("About.fxml");
    }

    private void updateActiveNavigation(String screen) {
        Button activeButton = switch (screen) {
            case "home.fxml", "CitizenHome.fxml" -> homeButton;
            case "ComplaintForm.fxml" -> newComplaintButton;
            case "ComplaintList.fxml" -> complaintsButton;
            case "Analytics.fxml" -> analyticsButton;
            case "map/MapView.fxml" -> mapButton;
            case "ComplaintTracking.fxml" -> trackingButton;
            case "Settings.fxml" -> settingsButton;
            case "About.fxml" -> aboutButton;
            default -> null;
        };
        if (activeButton != null) {
            setActiveNavigation(activeButton);
        }
    }

    private void setActiveNavigation(Button activeButton) {
        List.of(homeButton, newComplaintButton, complaintsButton, analyticsButton,
                mapButton, trackingButton, settingsButton, aboutButton, switchUserButton)
            .forEach(button -> button.getStyleClass().remove("menuButtonActive"));
        if (activeButton != null && !activeButton.getStyleClass().contains("menuButtonActive")) {
            activeButton.getStyleClass().add("menuButtonActive");
        }
    }

    @FXML
    private void switchUser() {
        User currentUser = UserSession.getLoggedUser();
        List<User> availableUsers = LoginController.getAvailableUsers().stream()
            .filter(user -> currentUser == null || !user.getId().equals(currentUser.getId()))
            .toList();

        if (availableUsers.isEmpty()) {
            return;
        }

        List<String> accountOptions = availableUsers.stream()
            .map(user -> user.getName() + " (" + user.getRole() + ")")
            .toList();
        ChoiceDialog<String> dialog = new ChoiceDialog<>(accountOptions.get(0), accountOptions);
        dialog.setTitle("Trocar usuário");
        dialog.setHeaderText("Escolha outro perfil para continuar");
        dialog.setContentText("Perfil:");
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.OK)).setText("Entrar");
        ((Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Cancelar");
        dialog.initOwner(mainRoot.getScene().getWindow());

        Optional<String> selection = dialog.showAndWait();
        if (selection.isEmpty()) {
            return;
        }

        User selectedUser = availableUsers.get(accountOptions.indexOf(selection.get()));
        try {
            UserSession.login(selectedUser);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Main.fxml"));
            Scene mainScene = new Scene(loader.load());
            Stage stage = (Stage) mainRoot.getScene().getWindow();

            stage.setScene(mainScene);
            stage.setFullScreen(true);
            stage.setMaximized(true);
        } catch (Exception e) {
            if (currentUser == null) {
                UserSession.logout();
            } else {
                UserSession.login(currentUser);
            }
            e.printStackTrace();
        }
    }

}
