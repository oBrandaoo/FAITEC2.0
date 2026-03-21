package org.example.controller;

import java.io.IOException;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.util.Duration;
import org.example.util.ScreenManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;

public class HomeController {
    @FXML
    private StackPane contentArea;
    @FXML
    private Parent root;
    
    @FXML
    public void initialize() {

        ScreenManager.setMainContainer(contentArea);

        ScreenManager.loadScreen("HomeContent.fxml");

        aplicarEmTodos(root);
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

    public void seeMap() {
        ScreenManager.loadScreen("MapView.fxml");
    }

    private void aplicarEmTodos(Parent parent) {
        for (Node node : parent.getChildrenUnmodifiable()) {

            if (node instanceof Button) {
                aplicarEfeito((Button) node);
            }

            if (node instanceof Parent) {
                aplicarEmTodos((Parent) node);
            }
        }
    }

    public void aplicarEfeito(Button botao) {

        ScaleTransition aumentar = new ScaleTransition(Duration.millis(150), botao);
        aumentar.setToX(1.1);
        aumentar.setToY(1.1);

        ScaleTransition diminuir = new ScaleTransition(Duration.millis(150), botao);
        diminuir.setToX(1.0);
        diminuir.setToY(1.0);

        botao.setOnMouseEntered(e -> aumentar.playFromStart());
        botao.setOnMouseExited(e -> diminuir.playFromStart());
    }
    
}
