package org.example.controller.maps;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.example.model.enums.MapMode;
import org.example.model.Location;
import org.example.util.MapBridge;

import java.util.function.Consumer;

public class MapController {

    @FXML
    private WebView mapView;

    @FXML
    private Label addressLabel;

    @FXML
    private Button confirmButton;

    private Stage stage;

    private Location selectedLocation;

    private WebEngine engine;

    private MapMode mode = MapMode.VIEW;

    private Consumer<Location> locationListener;

    @FXML
    public void initialize() {

        engine = mapView.getEngine();

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {

            if (newState == Worker.State.SUCCEEDED) {

                JSObject window = (JSObject) engine.executeScript("window");

                window.setMember(
                        "javaBridge",
                        new MapBridge(this)
                );

            }

        });

        engine.load(
                getClass()
                        .getResource("/map/map.html")
                        .toExternalForm()
        );
    }

    public void setMode(MapMode mode) {
        this.mode = mode;
    }

    public MapMode getMode() {
        return mode;
    }

    public void setLocationListener(Consumer<Location> listener) {
        this.locationListener = listener;
    }

    public void notifyLocation(Location location) {

        System.out.println("Location: " + location.getAddress());

        selectedLocation = location;

        addressLabel.setText(location.getAddress());

        confirmButton.setDisable(false);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void centerOn(double lat, double lng) {

        engine.executeScript(
                "centerMap(" + lat + "," + lng + ");"
        );
    }

    public void showLoading() {

        engine.executeScript(
                "showLoading();"
        );
    }

    public void hideLoading() {

        engine.executeScript(
                "hideLoading();"
        );
    }

    @FXML
    private void confirm() {

        if (selectedLocation == null) {
            return;
        }

        if (locationListener != null) {
            locationListener.accept(selectedLocation);
        }

        stage.close();
    }

    @FXML
    private void cancel() {

        stage.close();
    }

}