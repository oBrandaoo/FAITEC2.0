package org.example.service;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.util.Duration;
import org.example.model.Complaint;

import java.util.ArrayList;
import java.util.List;

public class ComplaintService {

    private static List<Complaint> complaints = new ArrayList<>();

    public static void addComplaint(Complaint complaint) {
        complaints.add(complaint);
    }

    public static List<Complaint> getAllComplaints() {
        return complaints;
    }

    public static void aplicarEmTodos(Node node) {
        if (node instanceof Button) {
            aplicarEfeito((Button) node);
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                aplicarEmTodos(child);
            }
        }
    }

    public static void aplicarEfeito(Button botao) {
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
