package org.example.util;

import org.example.model.User;
import org.example.model.enums.UserRole;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class ScreenManager {

    private static Pane mainContainer;

    public static void setMainContainer(Pane container) {
        mainContainer = container;
    }

    public static <T> T loadScreen(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource("/view/" + fxml));

            Node view = loader.load();
            mainContainer.getChildren().setAll(view);

            return loader.getController();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void loadHomeScreen() {
        User user = UserSession.getLoggedUser();
        String home = user != null && user.getRole() == UserRole.CIDADAO
            ? "CitizenHome.fxml" : "Home.fxml";
        loadScreen(home);
    }
}
