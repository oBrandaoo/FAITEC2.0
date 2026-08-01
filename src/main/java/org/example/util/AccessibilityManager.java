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
    private static boolean highContrast;
    private static boolean reducedMotion;

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

    public static boolean isHighContrast() {
        return highContrast;
    }

    public static void setHighContrast(boolean enabled) {
        highContrast = enabled;
        apply();
    }

    public static boolean isReducedMotion() {
        return reducedMotion;
    }

    public static void setReducedMotion(boolean enabled) {
        reducedMotion = enabled;
    }

    private static void apply() {
        if (applicationRoot == null) {
            return;
        }

        applicationRoot.getStyleClass().removeAll(
                "accessibility-font-large",
                "accessibility-font-extra-large",
                "accessibility-high-contrast"
        );

        if (fontSize == FontSize.GRANDE) {
            applicationRoot.getStyleClass().add("accessibility-font-large");
        } else if (fontSize == FontSize.EXTRA_GRANDE) {
            applicationRoot.getStyleClass().add("accessibility-font-extra-large");
        }

        if (highContrast) {
            applicationRoot.getStyleClass().add("accessibility-high-contrast");
        }
    }
}
