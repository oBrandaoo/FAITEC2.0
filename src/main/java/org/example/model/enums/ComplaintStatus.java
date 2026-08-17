package org.example.model.enums;

public enum ComplaintStatus {

    PENDENTE("Pendente", 0.20, "Recebemos o relato e ele aguarda a triagem inicial."),
    EM_ANALISE("Em análise", 0.45, "A equipe responsável está avaliando o problema."),
    EM_EXECUCAO("Em execução", 0.75, "O atendimento foi iniciado e está em andamento."),
    RESOLVIDO("Resolvido", 1.00, "O atendimento foi concluído pela equipe responsável."),
    CANCELADO("Cancelado", 1.00, "O acompanhamento foi encerrado por cancelamento.");

    private final String status;
    private final double trackingProgress;
    private final String trackingDescription;

    ComplaintStatus(String status, double trackingProgress, String trackingDescription) {
        this.status = status;
        this.trackingProgress = trackingProgress;
        this.trackingDescription = trackingDescription;
    }

    public String getStatus() {
        return status;
    }

    public double getTrackingProgress() {
        return trackingProgress;
    }

    public String getTrackingDescription() {
        return trackingDescription;
    }

    @Override
    public String toString() {
        return status;
    }
}
