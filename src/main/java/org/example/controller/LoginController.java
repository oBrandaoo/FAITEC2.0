package org.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField userField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void login(ActionEvent event) {

        String user = userField.getText();
        String password = passwordField.getText();

        if (user.equals("admin") && password.equals("1234")) {

            try {

                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/view/Main.fxml"));

                Scene scene = new Scene(loader.load());

                Stage stage = (Stage)((Button)event.getSource())
                        .getScene().getWindow();

                stage.setScene(scene);
                stage.setFullScreen(true);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {

            messageLabel.setText("Usuário ou senha inválidos.");

        }
    }

}