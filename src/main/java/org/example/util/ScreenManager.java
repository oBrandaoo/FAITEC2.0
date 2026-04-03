package org.example.util;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class ScreenManager {

    private static Pane mainContainer;

    public static void setMainContainer(Pane container) {
        mainContainer = container;
    }

    public static <T> T loadScreen(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    ScreenManager.class.getResource("/view/" + fxml)
            );

            Pane view = loader.load();
            mainContainer.getChildren().setAll(view);

            return loader.getController();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface DataReceiver<T> {
        void setData(T data);
    }
}
