package org.example.bridge;

import javafx.scene.web.WebEngine;

public class JavaBridge {

    private final WebEngine engine;

    public JavaBridge(WebEngine engine) {
        this.engine = engine;
    }

    public void goStart() {
        engine.load(
                getClass()
                        .getResource("/web/login/login.html")
                        .toExternalForm()
        );
    }

    public void newComplaint() {
        engine.load(
                getClass()
                        .getResource("/web/complaintForm/complaint.html")
                        .toExternalForm()
        );
    }

    public void seeComplaints() {
        engine.load(
                getClass()
                        .getResource("/web/complaintList/complaint.html")
                        .toExternalForm()
        );
    }

    public void seeMap() {
        engine.load(
                getClass()
                        .getResource("/web/map/map.html")
                        .toExternalForm()
        );
    }
}