package org.example.controller.maps;

import java.util.function.Consumer;

import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.enums.MapMode;
import org.example.service.ComplaintService;
import org.example.util.MapBridge;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

public class MapController {

    @FXML
    private WebView mapView;

    @FXML
    private Label addressLabel;

    @FXML
    private Button confirmButton;

    @FXML
    private Button cancelButton;

    private Stage stage;

    private Location selectedLocation;

    private WebEngine engine;

    private MapMode mode = MapMode.OVERVIEW;

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

                updateMapInteraction();

                if (mode == MapMode.OVERVIEW) {
                    showComplaintMarkers();
                }
            }

        });

        engine.load(
                getClass()
                        .getResource("/map/map.html")
                        .toExternalForm()
        );

        setMode(mode);
    }

    public void setMode(MapMode mode) {
        this.mode = mode;
        boolean selecting = mode == MapMode.SELECT;
        confirmButton.setVisible(selecting);
        confirmButton.setManaged(selecting);
        boolean showCloseButton = selecting || stage != null;
        cancelButton.setVisible(showCloseButton);
        cancelButton.setManaged(showCloseButton);
        cancelButton.setText(selecting ? "Cancelar" : "Fechar");
        updateMapInteraction();
    }

    public MapMode getMode() {
        return mode;
    }

    private void updateMapInteraction() {
        if (engine != null
                && engine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            engine.executeScript(
                    "setSelectionEnabled(" + (mode == MapMode.SELECT) + ");"
            );
        }
    }

    public void setLocationListener(Consumer<Location> listener) {
        this.locationListener = listener;
    }

    public void notifyLocation(Location location) {
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
                        + "addComplaintMarker(" + lat + "," + lng + ",'Local da reclamação');"
        );
    }

    public void setDisplayedLocation(Location location) {
        selectedLocation = location;
        addressLabel.setText(location.getAddress());

        Runnable centerAction = () -> centerOn(
                location.getLatitude(),
                location.getLongitude()
        );

        if (engine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            centerAction.run();
        } else {
            engine.getLoadWorker().stateProperty().addListener(
                    (observable, oldState, newState) -> {
                        if (newState == Worker.State.SUCCEEDED) {
                            centerAction.run();
                        }
                    }
            );
        }
    }

    private void showComplaintMarkers() {
        engine.executeScript("showPriorityLegend();");

        for (Complaint complaint : ComplaintService.getAllComplaints()) {
            Location location = complaint.getLocation();
            String popup = "<b>" + escapeHtml(complaint.getCategory().toString()) + "</b>"
                    + "<br>Prioridade: " + escapeHtml(complaint.getPriority().toString())
                    + "<br>Status: " + escapeHtml(complaint.getStatus().toString())
                    + "<br>" + escapeHtml(location.getAddress());

            engine.executeScript(
                    "addComplaintMarker("
                            + location.getLatitude() + ","
                            + location.getLongitude() + ","
                            + jsString(popup) + ","
                            + jsString(complaint.getPriority().name())
                            + ");"
            );
        }
    }

    private String escapeHtml(String value) {
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String jsString(String value) {
        return "'" + value
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\r", "\\r")
                .replace("\n", "\\n")
                + "'";
    }

    private boolean loading = false;

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void showLoading() {

        engine.executeScript(
                "showLoading(); setSelectionEnabled(false);"
        );
    }

    public void hideLoading() {

        engine.executeScript(
                "hideLoading();"
        );
        updateMapInteraction();
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
