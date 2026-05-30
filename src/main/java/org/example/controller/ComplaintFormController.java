package org.example.controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.example.bridge.JavaBridge;
import org.example.model.Complaint;
import org.example.model.enums.ComplaintCategory;

public class ComplaintFormController {

    @FXML
    private WebView webView;

    @FXML
    public void initialize() {

        WebEngine engine = webView.getEngine();

        engine.load(
                getClass()
                        .getResource("/web/complaint.html")
                        .toExternalForm()
        );

        engine.documentProperty().addListener((obs, oldDoc, newDoc) -> {

            if (newDoc != null) {

                JSObject window = (JSObject)
                        engine.executeScript("window");

                window.setMember(
                        "javaApp",
                        new JavaBridge(engine)
                );

                carregarCategorias();
            }
        });
    }

    private void carregarCategorias() {

        StringBuilder categorias = new StringBuilder("[");

        for (ComplaintCategory c : ComplaintCategory.values()) {

            categorias.append("'")
                    .append(c.name())
                    .append("',");
        }

        categorias.append("]");

        webView.getEngine().executeScript(
                "loadCategories(" + categorias + ")"
        );
    }

    public void submitComplaint(
            String categoria,
            String endereco,
            String descricao
    ) {

        Complaint complaint = new Complaint(
                ComplaintCategory.fromText(categoria),
                endereco,
                descricao
        );

        System.out.println("Reclamação enviada:");
        System.out.println(complaint);

        webView.getEngine().executeScript(
                "showSuccess()"
        );
    }
}