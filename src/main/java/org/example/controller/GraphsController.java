package org.example.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.example.model.Complaint;
import org.example.util.ScreenManager;

import java.util.HashMap;
import java.util.Map;

public class GraphsController {

    @FXML
    private WebView webView;

    private ObservableList<Complaint> complaintsList;

    @FXML
    public void initialize() {

        WebEngine engine = webView.getEngine();

        engine.load(
                getClass()
                        .getResource("/web/graphs.html")
                        .toExternalForm()
        );

        engine.documentProperty().addListener((obs, oldDoc, newDoc) -> {

            if (newDoc != null) {

                JSObject window = (JSObject)
                        engine.executeScript("window");

                window.setMember("javaApp", this);

                if (complaintsList != null) {
                    enviarDadosGrafico();
                }
            }
        });
    }

    public void setData(ObservableList<Complaint> lista) {
        this.complaintsList = lista;

        Platform.runLater(this::enviarDadosGrafico);
    }

    private void enviarDadosGrafico() {

        if (complaintsList == null) return;

        Map<String, Integer> contagem = new HashMap<>();

        for (Complaint c : complaintsList) {

            String categoria = c.getCategory().toString();

            contagem.put(
                    categoria,
                    contagem.getOrDefault(categoria, 0) + 1
            );
        }

        StringBuilder jsArray = new StringBuilder("[");

        contagem.forEach((cat, qtd) -> {

            jsArray.append("{")
                    .append("categoria:'").append(cat).append("',")
                    .append("quantidade:").append(qtd)
                    .append("},");
        });

        jsArray.append("]");

        webView.getEngine().executeScript(
                "renderChart(" + jsArray + ")"
        );
    }

    public void seeComplaints() {

        Platform.runLater(() ->
                ScreenManager.loadScreen("ComplaintList.fxml")
        );
    }
}