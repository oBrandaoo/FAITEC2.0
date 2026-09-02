package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp {

    public static void main(String[] args) {
        Application.launch(FxApplication.class, args);
    }

    public static class FxApplication extends Application {

        @Override
        public void start(Stage stage) throws Exception {
            FXMLLoader loader = new FXMLLoader(
                    MainApp.class.getResource("/view/Login.fxml")
            );

            Scene scene = new Scene(loader.load());
            stage.setTitle("Cidade em Dia");
            Image icon = new Image(
                    MainApp.class.getResourceAsStream("/images/logoSemNome.png")
            );
            stage.getIcons().add(icon);
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.show();
        }
    }
}
