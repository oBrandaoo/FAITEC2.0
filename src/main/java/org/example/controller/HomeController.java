package org.example.controller;

import java.io.IOException;

import javafx.animation.ScaleTransition;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import netscape.javascript.JSObject;
import org.example.util.ScreenManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

public class HomeController {
    @FXML
    private WebView mapView;

    @FXML
    public void initialize() {

        WebEngine engine = mapView.getEngine();

        engine.load(
                getClass().getResource("/web/home/home.html").toExternalForm()
        );

        engine.getLoadWorker().stateProperty().addListener(
                (obs, oldState, newState) -> {

                    if (newState == Worker.State.SUCCEEDED) {

                        JSObject window =
                                (JSObject) engine.executeScript("window");

                        window.setMember("javaApp", this);
                    }
                }
        );
    }
}
