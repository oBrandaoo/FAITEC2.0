package org.example.util;

import javafx.scene.Parent;

public final class AccessibilityManager {

    public enum FontSize {
        PADRAO("Padrão"),
        GRANDE("Grande"),
        EXTRA_GRANDE("Extra grande");

        private final String label;

        FontSize(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static Parent applicationRoot;
    private static FontSize fontSize = FontSize.PADRAO;

    private AccessibilityManager() {
    }

    public static void setApplicationRoot(Parent root) {
        applicationRoot = root;
        apply();
    }

    public static FontSize getFontSize() {
        return fontSize;
    }

    public static void setFontSize(FontSize size) {
        fontSize = size == null ? FontSize.PADRAO : size;
        apply();
    }

    private static void apply() {
        if (applicationRoot == null) {
            return;
        }

        applicationRoot.getStyleClass().removeAll(
            "accessibility-font-large",
            "accessibility-font-extra-large"
        );

        if (fontSize == FontSize.GRANDE) {
            applicationRoot.getStyleClass().add("accessibility-font-large");
        } else if (fontSize == FontSize.EXTRA_GRANDE) {
            applicationRoot.getStyleClass().add("accessibility-font-extra-large");
        }
    }
}
