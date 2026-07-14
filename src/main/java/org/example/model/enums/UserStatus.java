package org.example.model.enums;

public enum UserStatus {
    ATIVA("Ativa"),
    INATIVA("Inativa"),
    BLOQUEADA("Bloqueada");

    private final String status;

    UserStatus(String status) {
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
