package org.example.util;

import java.util.concurrent.CompletableFuture;

import org.example.controller.maps.MapController;
import org.example.model.Location;
import org.example.model.enums.MapMode;
import org.example.service.GeocodingService;

import javafx.application.Platform;

public class MapBridge {

    private final MapController controller;

    public MapBridge(MapController controller) {
        this.controller = controller;
    }

    public void onLocationSelected(double lat, double lng) {
        if (controller.getMode() != MapMode.SELECT) {
            return;
        }

        if (controller.isLoading()) {
            return;
        }

        controller.setLoading(true);
        controller.showLoading();

        Location fallback = new Location(lat, lng, String.format("Coordenadas: %.6f, %.6f", lat, lng));
        Platform.runLater(() -> controller.notifyLocation(fallback));

        CompletableFuture
                .supplyAsync(() -> GeocodingService.reverse(lat, lng)).thenAccept(location -> {
                    Platform.runLater(() -> {
                        controller.hideLoading();
                        controller.setLoading(false);

                        if (location != null) {
                            controller.notifyLocation(location);
                        }
                    });
                });
    }
}
