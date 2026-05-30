package org.example.controller;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.example.bridge.JavaBridge;

public class LoginController {

    @FXML
    private WebView webView;

    @FXML
    public void initialize() {

        WebEngine engine = webView.getEngine();

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {

            if (newState == Worker.State.SUCCEEDED) {

                JSObject window =
                        (JSObject) engine.executeScript("window");

                window.setMember(
                        "javaApp",
                        new JavaBridge(engine)
                );
            }
        });

        engine.load(
                getClass()
                        .getResource("/web/login/login.html")
                        .toExternalForm()
        );
    }
}