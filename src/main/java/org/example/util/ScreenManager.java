package org.example.util;

import org.example.model.User;
import org.example.model.enums.UserRole;

import java.util.function.Consumer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class ScreenManager {

    private static Pane mainContainer;
    private static Consumer<String> screenChangeListener;

    public static void setMainContainer(Pane container) {
        mainContainer = container;
    }

    public static void setScreenChangeListener(Consumer<String> listener) {
        screenChangeListener = listener;
    }

    public static <T> T loadScreen(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource("/view/" + fxml));

            Node view = loader.load();
            mainContainer.getChildren().setAll(view);
            if (screenChangeListener != null) {
                screenChangeListener.accept(fxml);
            }

            return loader.getController();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void loadHomeScreen() {
        User user = UserSession.getLoggedUser();
        String home = user != null && user.getRole() == UserRole.CIDADAO
            ? "CitizenHome.fxml" : "home.fxml";
        loadScreen(home);
    }
}
