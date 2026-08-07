package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));

        Scene scene = new Scene(loader.load());
        stage.setTitle("Cidade em Dia");
        Image icon = new Image(getClass().getResourceAsStream("/images/logoSemNome.png"));
        stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
