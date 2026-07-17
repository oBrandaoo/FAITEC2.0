package org.example.util;

import javafx.application.Platform;
import org.example.controller.maps.MapController;
import org.example.model.Location;
import org.example.service.GeocodingService;

import java.util.concurrent.CompletableFuture;

public class MapBridge {

    private final MapController controller;

    public MapBridge(MapController controller) {
        this.controller = controller;
    }

    public void onLocationSelected(double lat, double lng) {

        System.out.println("Clique recebido: " + lat + ", " + lng);

        if (controller.getMode() != org.example.model.enums.MapMode.SELECT) {
            return;
        }

        controller.showLoading();

        CompletableFuture
                .supplyAsync(() -> GeocodingService.reverse(lat, lng))
                .thenAccept(location -> {

                    Platform.runLater(() -> {

                        controller.hideLoading();

                        if (location != null) {
                            controller.notifyLocation(location);
                        } else {
                            System.out.println("Location nula");
                        }
                    });
                });
    }
}