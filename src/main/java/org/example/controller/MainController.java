package org.example.controller;

import org.example.util.ScreenManager;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {

        ScreenManager.setMainContainer(contentArea);

        ScreenManager.loadScreen("Home.fxml");
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

}
