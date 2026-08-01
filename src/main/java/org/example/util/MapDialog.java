package org.example.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.controller.maps.MapController;
import org.example.model.Location;
import org.example.model.enums.MapMode;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class MapDialog {

    public static Location show(Window owner) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    MapDialog.class.getResource("/view/map/MapView.fxml")
            );

            Parent root = loader.load();

            MapController controller = loader.getController();

            controller.setMode(MapMode.SELECT);

            Stage stage = new Stage();

            controller.setStage(stage);

            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.WINDOW_MODAL);

            stage.setTitle("Selecionar localização");

            stage.setScene(new Scene(root, 900, 600));

            AtomicReference<Location> selectedLocation =
                    new AtomicReference<>();

            controller.setLocationListener(location -> {
                selectedLocation.set(location);
            });

            stage.showAndWait();

            return selectedLocation.get();

        } catch (IOException e) {

            e.printStackTrace();

            return null;
        }
    }

    public static Location show() {
        return show(null);
    }

    public static void showLocation(Location location, Window owner) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    MapDialog.class.getResource("/view/map/MapView.fxml")
            );
            Parent root = loader.load();

            MapController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            controller.setMode(MapMode.VIEW);
            controller.setDisplayedLocation(location);

            if (owner != null) {
                stage.initOwner(owner);
            }
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Localização da reclamação");
            stage.setScene(new Scene(root, 900, 600));
            stage.showAndWait();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void showLocation(Location location) {
        showLocation(location, null);
    }
}
