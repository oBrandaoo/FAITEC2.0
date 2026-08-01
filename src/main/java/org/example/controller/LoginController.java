package org.example.controller;

import java.util.List;

import org.example.model.User;
import org.example.model.enums.UserRole;
import org.example.model.enums.UserStatus;
import org.example.util.UserSession;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    private static final List<User> USERS = List.of(
            new User("USR-001", "Administrador", "1234", UserStatus.ATIVA, UserRole.ADMINISTRADOR),
            new User("USR-002", "Atendente Municipal", "1234", UserStatus.ATIVA, UserRole.ATENDENTE),
            new User("USR-003", "Cidadão", "1234", UserStatus.ATIVA, UserRole.CIDADAO)
    );

    @FXML
    private TextField userField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    public void initialize() {
        userField.textProperty().addListener(
                (observable, oldValue, newValue) -> messageLabel.setText("")
        );
        passwordField.textProperty().addListener(
                (observable, oldValue, newValue) -> messageLabel.setText("")
        );
    }

    @FXML
    private void login(ActionEvent event) {

        String username = userField.getText().trim();
        String password = passwordField.getText();
        User authenticatedUser = authenticate(username, password);

        if (authenticatedUser != null) {

            try {
                UserSession.login(authenticatedUser);

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/view/Main.fxml"));

                Scene scene = new Scene(loader.load());

                Stage stage = (Stage)((Button)event.getSource())
                        .getScene().getWindow();

                stage.setScene(scene);
                stage.setFullScreen(true);
                stage.setMaximized(true);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            messageLabel.setText("Usuário ou senha inválidos.");

        }
    }

    private User authenticate(String username, String password) {
        return USERS.stream()
                .filter(user -> username.equals(usernameOf(user)))
                .filter(user -> user.passwordMatches(password))
                .filter(user -> user.getStatus() == UserStatus.ATIVA)
                .findFirst()
                .orElse(null);
    }

    private String usernameOf(User user) {
        return switch (user.getRole()) {
            case ADMINISTRADOR -> "admin";
            case ATENDENTE -> "atendente";
            case CIDADAO -> "cidadao";
        };
    }

}
