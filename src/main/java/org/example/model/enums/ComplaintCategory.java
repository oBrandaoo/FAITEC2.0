package org.example.model.enums;

public enum ComplaintCategory {

    BURACO_RUA("Buraco na rua"),
    ILUMINACAO_PUBLICA("Iluminação pública"),
    LIX0_ACUMULADO("Lixo acumulado"),
    ESGOTO("Esgoto"),
    SEGURANCA("Segurança");

    private final String description;

    ComplaintCategory(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static ComplaintCategory fromText(String text) {

        for (ComplaintCategory c : values()) {
            if (c.name().equalsIgnoreCase(text)) {
                return c;
            }

            if (c.getDescription().equalsIgnoreCase(text)) {
                return c;
            }
        }

        throw new IllegalArgumentException("Categoria inválida: " + text);
    }
}
