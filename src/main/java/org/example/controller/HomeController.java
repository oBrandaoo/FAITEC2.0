package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.example.bridge.JavaBridge;
import org.example.bridge.JavaBridgeSingleton;
import org.example.util.ScreenManager;

public class HomeController {

    @FXML
    private WebView webView;

    @FXML
    public void initialize() {

        WebEngine engine = webView.getEngine();

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {

            if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {

                JSObject window = (JSObject)
                        engine.executeScript("window");

                window.setMember(
                        "javaApp",
                        JavaBridgeSingleton.get()
                );

            }
        });

        engine.load(
                getClass()
                        .getResource("/web/home/home.html")
                        .toExternalForm()
        );
    }

}