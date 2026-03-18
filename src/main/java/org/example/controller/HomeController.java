package org.example.controller;

import java.io.IOException;

import javafx.scene.layout.Pane;
import org.example.util.ScreenManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

public class HomeController {
    @FXML
    private StackPane contentArea;
    @FXML
    private Button Inicio;
    @FXML
    private Button Reclamacoes;
    @FXML
    private Button Reclamacao;
    
    @FXML
    public void initialize() {

        ScreenManager.setMainContainer(contentArea);

        ScreenManager.loadScreen("HomeContent.fxml");

        Inicio.setOnMouseEntered(e -> {
            Inicio.setScaleX(1.1);
            Inicio.setScaleY(1.1);
        });
        Inicio.setOnMouseExited(e -> {
            Inicio.setScaleX(1.0);
            Inicio.setScaleY(1.0);
        });

        Reclamacao.setOnMouseEntered(e -> {
            Reclamacao.setScaleX(1.1);
            Reclamacao.setScaleY(1.1);
        });
        Reclamacao.setOnMouseExited(e -> {
            Reclamacao.setScaleX(1.0);
            Reclamacao.setScaleY(1.0);
        });

        Reclamacoes.setOnMouseEntered(e -> {
            Reclamacoes.setScaleX(1.1);
            Reclamacoes.setScaleY(1.1);
        });
        Reclamacoes.setOnMouseExited(e -> {
            Reclamacoes.setScaleX(1.0);
            Reclamacoes.setScaleY(1.0);
        });


    }

    public void newComplaint(ActionEvent event) throws IOException {

        ScreenManager.loadScreen("ComplaintForm.fxml");

    }

    public void seeComplaints(ActionEvent event) {

        ScreenManager.loadScreen("ComplaintList.fxml");
    }

    public void goHome() {

        ScreenManager.loadScreen("HomeContent.fxml");

    }
    
}
