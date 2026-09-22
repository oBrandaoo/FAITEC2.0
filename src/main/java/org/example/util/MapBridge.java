package org.example.util;

import java.util.concurrent.CompletableFuture;

import org.example.controller.maps.MapController;
import org.example.model.Location;
import org.example.model.enums.MapMode;
import org.example.service.GeocodingService;

import javafx.application.Platform;

public class MapBridge {

    private final MapController controller;
    private String cityBoundaryGeoJson;
    private boolean cityBoundaryRequestInProgress;

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

    public void onMapFiltersChanged(String scope, String priority) {
        Platform.runLater(() -> controller.setMapFilters(scope, priority));
    }

    public void onCityBoundaryRequested() {
        if (cityBoundaryGeoJson != null) {
            Platform.runLater(() -> controller.showCityBoundary(cityBoundaryGeoJson));
            return;
        }
        if (cityBoundaryRequestInProgress) {
            return;
        }

        cityBoundaryRequestInProgress = true;
        CompletableFuture
                .supplyAsync(GeocodingService::searchCityBoundaryGeoJson)
                .thenAccept(geoJson -> Platform.runLater(() -> {
                    cityBoundaryRequestInProgress = false;
                    if (geoJson == null || geoJson.isBlank()) {
                        controller.showCityBoundaryError();
                        return;
                    }
                    cityBoundaryGeoJson = geoJson;
                    controller.showCityBoundary(geoJson);
                }));
    }
}
