package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.example.bridge.JavaBridgeSingleton;
import org.example.util.ScreenManager;

public class StartController {

    @FXML
    private WebView webView;

    @FXML
    public void initialize() {

        WebEngine engine = webView.getEngine();

        engine.load(
                getClass()
                        .getResource("/web/start/start.html")
                        .toExternalForm()
        );

        engine.documentProperty().addListener((obs, oldDoc, newDoc) -> {

            if (newDoc != null) {

                JSObject window = (JSObject)
                        engine.executeScript("window");

                window.setMember("javaApp", JavaBridgeSingleton.get());
            }
        });
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