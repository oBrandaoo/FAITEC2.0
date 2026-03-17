package org.example.controller;

import javafx.scene.layout.StackPane;
import org.example.util.ScreenManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.io.IOException;

public class HomeController {
    @FXML
    private StackPane contentArea;
    @FXML
    private Button main_button;
    @FXML
    private Button main_button2;
    @FXML
    public void initialize() {

        ScreenManager.setMainContainer(contentArea);
        main_button.setOnMouseEntered(e -> {
            main_button.setScaleX(1.4);
            main_button.setScaleY(1.4);
        });

        main_button.setOnMouseExited(e -> {
            main_button.setScaleX(1.0);
            main_button.setScaleY(1.0);
        });
        main_button2.setOnMouseEntered(e -> {
            main_button2.setScaleX(1.4);
            main_button2.setScaleY(1.4);
        });

        main_button2.setOnMouseExited(e -> {
            main_button2.setScaleX(1.0);
            main_button2.setScaleY(1.0);
        });
    }

    public void newComplaint(ActionEvent event) throws IOException {

        ScreenManager.loadScreen("ComplaintForm.fxml");

    }

    public void seeComplaints(ActionEvent event) {

        ScreenManager.loadScreen("ComplaintList.fxml");
    }
}
