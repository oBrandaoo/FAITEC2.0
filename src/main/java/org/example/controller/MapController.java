package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

public class MapController {

    @FXML
    private WebView mapView;

    @FXML
    public void initialize() {

        WebEngine engine = mapView.getEngine();

        engine.load(
                getClass().getResource("/web/map/map.html").toExternalForm()
        );
    }
}