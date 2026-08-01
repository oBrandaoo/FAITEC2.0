package org.example.controller;

import org.example.model.User;
import org.example.util.UserSession;
import org.example.util.AccessibilityManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

public class SettingsController {

    @FXML private Label avatarLabel;
    @FXML private Label nameLabel;
    @FXML private Label idLabel;
    @FXML private Label statusLabel;
    @FXML private Label roleLabel;
    @FXML private ComboBox<AccessibilityManager.FontSize> fontSizeBox;
    @FXML private CheckBox reducedMotionCheckBox;

    @FXML
    public void initialize() {
        configureAccessibility();

        User user = UserSession.getLoggedUser();
        if (user == null) {
            nameLabel.setText("Nenhum usuário autenticado");
            idLabel.setText("-");
            statusLabel.setText("-");
            roleLabel.setText("-");
            avatarLabel.setText("?");
            return;
        }

        nameLabel.setText(user.getName());
        idLabel.setText(user.getId());
        statusLabel.setText(user.getStatus().toString());
        roleLabel.setText(user.getRole().toString());
        avatarLabel.setText(initialsOf(user.getName()));
    }

    private void configureAccessibility() {
        fontSizeBox.getItems().setAll(AccessibilityManager.FontSize.values());
        fontSizeBox.setValue(AccessibilityManager.getFontSize());
        reducedMotionCheckBox.setSelected(AccessibilityManager.isReducedMotion());

        fontSizeBox.valueProperty().addListener(
                (observable, oldValue, newValue) -> AccessibilityManager.setFontSize(newValue)
        );
        reducedMotionCheckBox.selectedProperty().addListener(
                (observable, oldValue, selected) -> AccessibilityManager.setReducedMotion(selected)
        );
    }

    @FXML
    private void logout(ActionEvent event) {
        try {
            UserSession.logout();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setFullScreen(false);
            stage.setMaximized(true);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private String initialsOf(String name) {
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isBlank()) {
            return "?";
        }
        String first = parts[0].substring(0, 1);
        String last = parts.length > 1 ? parts[parts.length - 1].substring(0, 1) : "";
        return (first + last).toUpperCase();
    }
}
