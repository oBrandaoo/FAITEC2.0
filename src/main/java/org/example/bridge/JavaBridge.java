package org.example.bridge;

import org.example.util.ScreenManager;

public class JavaBridge {

    public void goStart() {
        ScreenManager.loadScreen("Start.fxml");
    }

    public void newComplaint() {
        ScreenManager.loadScreen("ComplaintForm.fxml");
    }

    public void seeComplaints() {
        ScreenManager.loadScreen("ComplaintList.fxml");
    }

    public void seeMap() {
        ScreenManager.loadScreen("Map.fxml");
    }
}