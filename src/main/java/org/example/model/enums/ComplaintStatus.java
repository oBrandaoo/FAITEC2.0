package org.example.model.enums;

public enum ComplaintStatus {

    PENDENTE("Pendente"),
    EM_ANALISE("Em análise"),
    EM_EXECUCAO("Em execução"),
    RESOLVIDO("Resolvido"),
    CANCELADO("Cancelado");

    private final String status;

    ComplaintStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }
}
