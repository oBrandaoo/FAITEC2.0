package org.example.controller;

import java.io.IOException;

import javafx.scene.layout.Pane;
import org.example.util.ScreenManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class HomeController {
    @FXML
    private StackPane contentArea;
    @FXML
    private Pane mainContainer;
    
    @FXML
    public void initialize() {

        ScreenManager.setMainContainer(contentArea);

        ScreenManager.loadScreen("HomeContent.fxml");

    }

    public void newComplaint(ActionEvent event) throws IOException {

        ScreenManager.loadScreen("ComplaintForm.fxml");

    }

    public void seeComplaints(ActionEvent event) {

        ScreenManager.loadScreen("ComplaintList.fxml");
    }

    public void goHome() {

        ScreenManager.loadScreen("HomeContent.fxml");

    }
    
}
