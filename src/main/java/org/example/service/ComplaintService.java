package org.example.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.User;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.example.model.enums.ComplaintSubcategory;

import javafx.animation.ScaleTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class ComplaintService {

    private static final List<Complaint> complaints = new ArrayList<>();

    static {
        loadMockComplaints();
    }

    private static void loadMockComplaints() {
        complaints.add(new Complaint(
                ComplaintCategory.BURACO_RUA,
                ComplaintSubcategory.BURACO_EM_VIA,
                new Location(
                        -22.252218,
                        -45.703128,
                        "Avenida Sinhá Moreira, 125 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "Buraco grande próximo à faixa de pedestres.",
                ComplaintStatus.PENDENTE,
                ComplaintPriority.URGENTE,
                LocalDate.now().minusDays(2),
                "USR-003",
                "Cidadão"
        ));

        complaints.add(new Complaint(
                ComplaintCategory.ILUMINACAO_PUBLICA,
                ComplaintSubcategory.POSTE_APAGADO,
                new Location(
                        -22.251156,
                        -45.702279,
                        "Praça Delfim Moreira, 40 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "Dois postes estão apagados há vários dias.",
                ComplaintStatus.EM_ANALISE,
                ComplaintPriority.ALTA,
                LocalDate.now().minusDays(5)
        ));

        complaints.add(new Complaint(
                ComplaintCategory.LIX0_ACUMULADO,
                ComplaintSubcategory.DESCARTE_IRREGULAR,
                new Location(
                        -22.254512,
                        -45.701324,
                        "Alameda José Cleto Duarte, 80 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "Há sacos, caixas e móveis descartados na calçada.",
                ComplaintStatus.EM_EXECUCAO,
                ComplaintPriority.MEDIA,
                LocalDate.now().minusDays(7),
                "USR-003",
                "Cidadão"
        ));

        complaints.add(new Complaint(
                ComplaintCategory.ESGOTO,
                ComplaintSubcategory.VAZAMENTO_ESGOTO,
                new Location(
                        -22.255896,
                        -45.705117,
                        "Rua Padre Vítor, 310 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "Vazamento de esgoto com mau cheiro em frente às residências.",
                ComplaintStatus.RESOLVIDO,
                ComplaintPriority.URGENTE,
                LocalDate.now().minusDays(12)
        ));

        complaints.add(new Complaint(
                ComplaintCategory.SEGURANCA,
                ComplaintSubcategory.PONTO_INSEGURO,
                new Location(
                        -22.247816,
                        -45.699462,
                        "Avenida Embaixador Bilac Pinto, 780 - Santa Rita do Sapucaí/MG"
                ),
                "Solicitação de melhoria na iluminação do ponto de ônibus.",
                ComplaintStatus.CANCELADO,
                ComplaintPriority.BAIXA,
                LocalDate.now().minusDays(15)
        ));

        complaints.add(new Complaint(
                ComplaintCategory.BURACO_RUA,
                ComplaintSubcategory.ASFALTO_DANIFICADO,
                new Location(
                        -22.258044,
                        -45.696851,
                        "Praça Expedicionário Maurício Adami, 22 - Eletrônica, Santa Rita do Sapucaí/MG"
                ),
                "Asfalto danificado após a chuva; o local oferece risco a motociclistas.",
                ComplaintStatus.PENDENTE,
                ComplaintPriority.ALTA,
                LocalDate.now().minusDays(1),
                "USR-003",
                "Cidadão"
        ));

        complaints.add(new Complaint(
                ComplaintCategory.TRANSITO_MOBILIDADE,
                ComplaintSubcategory.SEMAFORO_DEFEITO,
                new Location(
                        -22.252830,
                        -45.703930,
                        "Rua Cel. Joaquim Neto, 320 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "Semáforo intermitente causa risco no cruzamento em horários de pico.",
                ComplaintStatus.PENDENTE,
                ComplaintPriority.URGENTE,
                LocalDate.now().minusDays(3),
                "USR-003",
                "Cidadão"
        ));

        // Additional records keep the map useful for demonstrating the owner
        // and priority filters with every mock user.
        complaints.add(new Complaint(
                ComplaintCategory.BURACO_RUA,
                ComplaintSubcategory.BURACO_EM_VIA,
                new Location(
                        -22.252310,
                        -45.703080,
                        "Avenida Sinhá Moreira, 138 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "Outro morador identificou o mesmo buraco próximo à faixa.",
                ComplaintStatus.PENDENTE,
                ComplaintPriority.ALTA,
                LocalDate.now().minusDays(1),
                "USR-001",
                "Administrador"
        ));

        complaints.add(new Complaint(
                ComplaintCategory.BURACO_RUA,
                ComplaintSubcategory.BURACO_EM_VIA,
                new Location(
                        -22.252105,
                        -45.703205,
                        "Avenida Sinhá Moreira, 116 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "Buraco aumentando e dificultando a travessia de pedestres.",
                ComplaintStatus.EM_ANALISE,
                ComplaintPriority.MEDIA,
                LocalDate.now().minusDays(4),
                "USR-002",
                "Atendente Municipal"
        ));

        complaints.add(new Complaint(
                ComplaintCategory.ILUMINACAO_PUBLICA,
                ComplaintSubcategory.LAMPADA_OSCILANDO,
                new Location(
                        -22.250842,
                        -45.704218,
                        "Rua Antônio de Cássia, 55 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "Lâmpada oscila durante a noite e deixa a calçada escura.",
                ComplaintStatus.EM_ANALISE,
                ComplaintPriority.ALTA,
                LocalDate.now().minusDays(6),
                "USR-001",
                "Administrador"
        ));

        complaints.add(new Complaint(
                ComplaintCategory.ESGOTO,
                ComplaintSubcategory.BUEIRO_ENTUPIDO,
                new Location(
                        -22.253428,
                        -45.706114,
                        "Rua Francisco Andrade, 205 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "Bueiro entupido acumula água depois das chuvas.",
                ComplaintStatus.PENDENTE,
                ComplaintPriority.MEDIA,
                LocalDate.now().minusDays(8),
                "USR-001",
                "Administrador"
        ));

        complaints.add(new Complaint(
                ComplaintCategory.TRANSITO_MOBILIDADE,
                ComplaintSubcategory.SINALIZACAO_AUSENTE,
                new Location(
                        -22.256704,
                        -45.699846,
                        "Rua Barão do Rio Branco, 90 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "A sinalização da conversão está apagada no cruzamento.",
                ComplaintStatus.PENDENTE,
                ComplaintPriority.URGENTE,
                LocalDate.now().minusDays(2),
                "USR-002",
                "Atendente Municipal"
        ));

        complaints.add(new Complaint(
                ComplaintCategory.LIX0_ACUMULADO,
                ComplaintSubcategory.COLETA_ATRASADA,
                new Location(
                        -22.246952,
                        -45.706884,
                        "Rua João Rennó, 410 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "A coleta está atrasada há três dias nesta rua.",
                ComplaintStatus.EM_EXECUCAO,
                ComplaintPriority.MEDIA,
                LocalDate.now().minusDays(9),
                "USR-002",
                "Atendente Municipal"
        ));

        complaints.add(new Complaint(
                ComplaintCategory.ILUMINACAO_PUBLICA,
                ComplaintSubcategory.FIACAO_EXPOSTA,
                new Location(
                        -22.260116,
                        -45.704252,
                        "Avenida Frederico de Paula Cunha, 670 - Eletrônica, Santa Rita do Sapucaí/MG"
                ),
                "Fiação exposta em frente ao ponto de ônibus.",
                ComplaintStatus.PENDENTE,
                ComplaintPriority.URGENTE,
                LocalDate.now().minusDays(3),
                "USR-002",
                "Atendente Municipal"
        ));

        complaints.add(new Complaint(
                ComplaintCategory.SEGURANCA,
                ComplaintSubcategory.VANDALISMO,
                new Location(
                        -22.249126,
                        -45.700976,
                        "Praça Santa Rita, 12 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "Banco e placa da praça foram danificados.",
                ComplaintStatus.RESOLVIDO,
                ComplaintPriority.BAIXA,
                LocalDate.now().minusDays(18),
                "USR-001",
                "Administrador"
        ));

        complaints.add(new Complaint(
                ComplaintCategory.ESGOTO,
                ComplaintSubcategory.MAU_CHEIRO,
                new Location(
                        -22.251934,
                        -45.701706,
                        "Rua Silvestre Ferraz, 145 - Centro, Santa Rita do Sapucaí/MG"
                ),
                "Mau cheiro constante vindo da rede de esgoto.",
                ComplaintStatus.EM_ANALISE,
                ComplaintPriority.ALTA,
                LocalDate.now().minusDays(5),
                "USR-003",
                "Cidadão"
        ));

        complaints.add(new Complaint(
                ComplaintCategory.BURACO_RUA,
                ComplaintSubcategory.ASFALTO_DANIFICADO,
                new Location(
                        -22.258188,
                        -45.696742,
                        "Rua José Pinto de Souza, 31 - Eletrônica, Santa Rita do Sapucaí/MG"
                ),
                "Trecho de asfalto danificado perto da escola.",
                ComplaintStatus.PENDENTE,
                ComplaintPriority.BAIXA,
                LocalDate.now().minusDays(11),
                "USR-003",
                "Cidadão"
        ));
    }

    public static void addComplaint(Complaint complaint) {
        complaints.add(complaint);
    }

    public static List<Complaint> getAllComplaints() {
        return complaints;
    }

    public static List<Complaint> getComplaintsFor(User user) {
        if (user == null) {
            return List.of();
        }
        if (user.getRole().canManageComplaints()) {
            return new ArrayList<>(complaints);
        }
        return complaints.stream()
                .filter(complaint -> user.getId().equals(complaint.getCreatorId())).toList();
    }

    public static List<Complaint> getTrackableComplaints(User user) {
        if (user == null) {
            return List.of();
        }
        return new ArrayList<>(complaints);
    }

    public static boolean isWithinDateRange(Complaint complaint, LocalDate startDate, LocalDate endDate) {
        if (complaint == null || complaint.getDate() == null) {
            return false;
        }
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return false;
        }
        return (startDate == null || !complaint.getDate().isBefore(startDate))
                && (endDate == null || !complaint.getDate().isAfter(endDate));
    }

    public static void updateStatus(Complaint complaint, ComplaintStatus newStatus,
        User responsible, String note) {
        if (complaint == null || newStatus == null) {
            return;
        }

        String responsibleName = responsible == null ? "Sistema" : responsible.getName();
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
