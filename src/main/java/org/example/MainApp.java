package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/view/Home.fxml")
        );

        //Font.loadFont(getClass().getResourceAsStream("/fonts/TextMeOne.ttf"),20);
        //Parent root = loader.load();

        //Scene scene = new Scene(root);
        //String css = this.getClass().getResource("/css/style.css").toExternalForm();
        //scene.getStylesheets().add(css);
        stage.setTitle("Cidade Melhor");
        //stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}