package org.example.model.enums;

public enum ComplaintPriority {
    BAIXA("Baixa"),
    MEDIA("Média"),
    ALTA("Alta"),
    URGENTE("Urgente");

    private final String description;

    ComplaintPriority(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
