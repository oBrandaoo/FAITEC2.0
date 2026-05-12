package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.example.model.Complaint;
import org.example.model.enums.ComplaintStatus;
import org.example.service.ComplaintService;
import org.example.util.ScreenManager;

public class ComplaintListController {

    @FXML
    private WebView webView;

    private ObservableList<Complaint> complaints;

    @FXML
    public void initialize() {

        WebEngine engine = webView.getEngine();

        engine.load(
                getClass()
                        .getResource("/web/complaint-list.html")
                        .toExternalForm()
        );

        engine.documentProperty().addListener((obs, oldDoc, newDoc) -> {

            if (newDoc != null) {

                JSObject window = (JSObject)
                        engine.executeScript("window");

                window.setMember("javaApp", this);

                carregarTabela();
            }
        });
    }

    private void carregarTabela() {

        complaints = FXCollections.observableArrayList(
                ComplaintService.getAllComplaints()
        );

        StringBuilder json = new StringBuilder("[");

        for (Complaint c : complaints) {

            json.append("{")
                    .append("categoria:'").append(c.getCategory()).append("',")
                    .append("local:'").append(c.getLocation()).append("',")
                    .append("descricao:'").append(c.getDescription()).append("',")
                    .append("status:'").append(c.getStatus()).append("',")
                    .append("data:'").append(c.getDate()).append("'")
                    .append("},");
        }

        json.append("]");

        webView.getEngine().executeScript(
                "renderTable(" + json + ")"
        );
    }

    public void resolver(int index) {

        Complaint complaint = complaints.get(index);

        complaint.setStatus(
                ComplaintStatus.RESOLVIDO
        );

        carregarTabela();
    }

    public void seeGraphs() {

        ScreenManager.loadScreen("Graphs.fxml");
    }

    public void goStart() {

        ScreenManager.loadScreen("Home.fxml");
    }
}
