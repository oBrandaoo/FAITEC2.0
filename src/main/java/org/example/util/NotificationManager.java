package org.example.util;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public final class NotificationManager {

    private static VBox container;

    private NotificationManager() {
    }

    public static void setContainer(VBox notificationContainer) {
        container = notificationContainer;
        container.getChildren().clear();
    }

    public static void success(String message) {
        show("Sucesso", message, "✓", "toast-success");
    }

    public static void error(String message) {
        show("Erro", message, "×", "toast-error");
    }

    public static void warning(String message) {
        show("Atenção", message, "!", "toast-warning");
    }

    public static void info(String message) {
        show("Informação", message, "i", "toast-info");
    }

    private static void show(String title, String message, String icon, String typeClass) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> show(title, message, icon, typeClass));
            return;
        }
        if (container == null) {
            return;
        }

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("toast-icon");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("toast-title");

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(310);
        messageLabel.getStyleClass().add("toast-message");

        VBox text = new VBox(3, titleLabel, messageLabel);
        HBox toast = new HBox(12, iconLabel, text);
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.setOpacity(0);
        toast.getStyleClass().addAll("toast", typeClass);
        container.getChildren().add(0, toast);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(180), toast);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        PauseTransition visible = new PauseTransition(Duration.seconds(3.5));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(280), toast);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(event -> container.getChildren().remove(toast));

        new SequentialTransition(fadeIn, visible, fadeOut).play();
    }
}
