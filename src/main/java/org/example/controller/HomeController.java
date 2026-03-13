package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;

public class HomeController {
    @FXML
    private VBox contentArea;

    public void newComplaint(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/complaint.fxml")
        );

        VBox complaintView = loader.load();

        contentArea.getChildren().setAll(complaintView);

    }

    public void seeComplaints(ActionEvent event) {

        System.out.println("Abrir lista de reclamações");

    }

}