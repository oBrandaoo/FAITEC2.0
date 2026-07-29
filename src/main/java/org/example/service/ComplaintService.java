package org.example.service;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.util.Duration;
import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.User;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ComplaintService {

    private static final List<Complaint> complaints = new ArrayList<>();

    static {
        loadMockComplaints();
    }

    private static void loadMockComplaints() {
        complaints.add(new Complaint(
                ComplaintCategory.BURACO_RUA,
                new Location(-23.5505, -46.6333, "Rua das Flores, 125 - Centro"),
                "Buraco grande próximo à faixa de pedestres.",
                ComplaintStatus.PENDENTE,
                ComplaintPriority.URGENTE,
                LocalDate.now().minusDays(2)
        ));

        complaints.add(new Complaint(
                ComplaintCategory.ILUMINACAO_PUBLICA,
                new Location(-23.5489, -46.6388, "Avenida Brasil, 820 - Jardim América"),
                "Dois postes estão apagados há vários dias.",
                ComplaintStatus.EM_ANALISE,
                ComplaintPriority.ALTA,
                LocalDate.now().minusDays(5)
        ));

        complaints.add(new Complaint(
                ComplaintCategory.LIX0_ACUMULADO,
                new Location(-23.5572, -46.6251, "Praça da República, 40 - Centro"),
                "Há sacos, caixas e móveis descartados na calçada.",
                ComplaintStatus.EM_EXECUCAO,
                ComplaintPriority.MEDIA,
                LocalDate.now().minusDays(7)
        ));

        complaints.add(new Complaint(
                ComplaintCategory.ESGOTO,
                new Location(-23.5621, -46.6544, "Rua São José, 310 - Bela Vista"),
                "Vazamento de esgoto com mau cheiro em frente às residências.",
                ComplaintStatus.RESOLVIDO,
                ComplaintPriority.URGENTE,
                LocalDate.now().minusDays(12)
        ));

        complaints.add(new Complaint(
                ComplaintCategory.SEGURANCA,
                new Location(-23.5454, -46.6208, "Travessa Esperança, 18 - Brás"),
                "Solicitação de melhoria na iluminação do ponto de ônibus.",
                ComplaintStatus.CANCELADO,
                ComplaintPriority.BAIXA,
                LocalDate.now().minusDays(15)
        ));

        complaints.add(new Complaint(
                ComplaintCategory.BURACO_RUA,
                new Location(-23.5648, -46.6423, "Alameda Santos, 1500 - Cerqueira César"),
                "Asfalto danificado após a chuva; o local oferece risco a motociclistas.",
                ComplaintStatus.PENDENTE,
                ComplaintPriority.ALTA,
                LocalDate.now().minusDays(1)
        ));
    }

    public static void addComplaint(Complaint complaint) {
        complaints.add(complaint);
    }

    public static List<Complaint> getAllComplaints() {
        return complaints;
    }

    public static void updateStatus(
            Complaint complaint,
            ComplaintStatus newStatus,
            User responsible,
            String note
    ) {
        if (complaint == null || newStatus == null) {
            return;
        }

        String responsibleName = responsible == null
                ? "Sistema"
                : responsible.getName();
        complaint.changeStatus(newStatus, responsibleName, note);
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
