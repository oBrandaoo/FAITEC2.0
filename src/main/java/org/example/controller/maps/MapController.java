package org.example.controller.maps;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.enums.MapMode;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.example.service.ComplaintClusterService;
import org.example.service.ComplaintMapFilterService;
import org.example.service.ComplaintService;
import org.example.util.MapBridge;
import org.example.util.NotificationManager;
import org.example.util.UserSession;

import javafx.concurrent.Worker;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
import netscape.javascript.JSObject;

public class MapController {

    @FXML private WebView mapView;
    @FXML private Label addressLabel;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    private Stage stage;
    private Location selectedLocation;
    private WebEngine engine;
    private final MapBridge mapBridge = new MapBridge(this);
    private MapMode mode = MapMode.OVERVIEW;
    private Consumer<Location> locationListener;
    private boolean mineOnly;
    private ComplaintPriority selectedPriority;
    private ComplaintStatus selectedStatus;
    private LocalDate filterStartDate;
    private LocalDate filterEndDate;
    private boolean loading;
    private final PauseTransition mapResizeDebounce =
            new PauseTransition(Duration.millis(100));

    @FXML
    public void initialize() {
        engine = mapView.getEngine();

        mapResizeDebounce.setOnFinished(event -> invalidateMapSize());
        mapView.widthProperty().addListener((obs, oldValue, newValue) -> scheduleMapResize());
        mapView.heightProperty().addListener((obs, oldValue, newValue) -> scheduleMapResize());

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                // WebEngine keeps only a weak reference to Java objects exposed to
                // JavaScript. Retaining the bridge here prevents filter callbacks
                // from disappearing after garbage collection.
                window.setMember("javaBridge", mapBridge);
                engine.executeScript("initializeMapViewControls();");
                engine.executeScript("initializeMapFilters();");
                engine.executeScript("setMapFiltersVisible(" + (mode == MapMode.OVERVIEW) + ");");
                updateMapInteraction();
                Platform.runLater(this::scheduleMapResize);

                if (mode == MapMode.OVERVIEW) {
                    showComplaintMarkers();
                }
            }
        });

        engine.load(getClass().getResource("/map/map.html").toExternalForm());
        setMode(mode);
    }

    private void scheduleMapResize() {
        mapResizeDebounce.playFromStart();
    }

    private void invalidateMapSize() {
        if (engine != null
                && engine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            engine.executeScript("invalidateMapSize();");
        }
    }

    public void setMode(MapMode mode) {
        this.mode = mode;
        boolean selecting = mode == MapMode.SELECT;
        boolean showFilters = mode == MapMode.OVERVIEW;
        confirmButton.setVisible(selecting);
        confirmButton.setManaged(selecting);
        boolean showCloseButton = selecting || stage != null;
        cancelButton.setVisible(showCloseButton);
        cancelButton.setManaged(showCloseButton);
        cancelButton.setText(selecting ? "Cancelar" : "Fechar");
        updateMapInteraction();

        if (engine != null
                && engine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            engine.executeScript("setMapFiltersVisible(" + showFilters + ");");
            if (showFilters) {
                showComplaintMarkers();
            } else {
                engine.executeScript("clearComplaintMarkers();");
            }
        }
    }

    public MapMode getMode() {
        return mode;
    }

    private void updateMapInteraction() {
        if (engine != null
                && engine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            engine.executeScript("setSelectionEnabled(" + (mode == MapMode.SELECT) + ");");
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

    public void setMapFilters(String scope, String priorityName, String statusName,
            String period, String customStart, String customEnd) {
        mineOnly = "mine".equals(scope);
        selectedPriority = null;
        if (priorityName != null && !priorityName.isBlank() && !"ALL".equals(priorityName)) {
            try {
                selectedPriority = ComplaintPriority.valueOf(priorityName);
            } catch (IllegalArgumentException ignored) {
                selectedPriority = null;
            }
        }

        selectedStatus = null;
        if (statusName != null && !statusName.isBlank() && !"ALL".equals(statusName)) {
            try {
                selectedStatus = ComplaintStatus.valueOf(statusName);
            } catch (IllegalArgumentException ignored) {
                selectedStatus = null;
            }
        }

        filterStartDate = null;
        filterEndDate = null;
        if ("7".equals(period) || "30".equals(period) || "90".equals(period)) {
            int days = Integer.parseInt(period);
            filterStartDate = LocalDate.now().minusDays(days - 1L);
            filterEndDate = LocalDate.now();
        } else if ("CUSTOM".equals(period)) {
            filterStartDate = parseDate(customStart);
            filterEndDate = parseDate(customEnd);
            if (filterStartDate != null && filterEndDate != null
                    && filterStartDate.isAfter(filterEndDate)) {
                NotificationManager.warning("A data inicial deve ser anterior à data final.");
            }
        }
        refreshComplaintMarkers();
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (java.time.format.DateTimeParseException ignored) {
            return null;
        }
    }

    public void showCityBoundary(String geoJson) {
        if (engine == null
                || engine.getLoadWorker().getState() != Worker.State.SUCCEEDED) {
            return;
        }
        if (geoJson == null || geoJson.isBlank()) {
            engine.executeScript(
                    "showCityBoundaryError('Não foi possível carregar os limites da cidade.');");
            return;
        }
        engine.executeScript("showCityBoundary(" + jsString(geoJson) + ");");
    }

    public void showCityBoundaryError() {
        if (engine != null
                && engine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            engine.executeScript(
                    "showCityBoundaryError('Não foi possível carregar os limites da cidade.');");
        }
    }

    public void centerOn(double lat, double lng) {
        engine.executeScript(
                "centerMap(" + lat + "," + lng + ");"
                        + "addComplaintMarker(" + lat + "," + lng
                        + ",'Local da reclamação','MEDIA',1);"
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
        engine.executeScript("clearComplaintMarkers(); showPriorityLegend();");

        var currentUser = UserSession.getLoggedUser();
        var filteredComplaints = getFilteredComplaints(currentUser);

        for (var cluster : ComplaintClusterService.groupByProximity(
                filteredComplaints)) {
            Complaint complaint = cluster.highestPriorityComplaint();
            Location location = complaint.getLocation();
            if (location == null) {
                continue;
            }

            String popup = "<b>" + escapeHtml(complaint.getCategory().toString()) + "</b>"
                    + (complaint.getSubcategory() == null
                            ? ""
                            : "<br>" + escapeHtml(complaint.getSubcategory().toString()))
                    + "<br>" + cluster.count() + (cluster.count() == 1
                            ? " registro neste grupo"
                            : " registros deste problema em um raio de 40 m")
                    + "<br>Prioridade mais alta: " + escapeHtml(complaint.getPriority().toString())
                    + "<br>" + escapeHtml(location.getAddress())
                    + "<br>Status: " + escapeHtml(complaint.getStatus().toString());

            boolean hasConfirmed = complaint.hasResolutionConfirmationFrom(
                    currentUser == null ? null : currentUser.getId());

            engine.executeScript(
                    "addComplaintMarker("
                            + cluster.latitude() + ","
                            + cluster.longitude() + ","
                            + jsString(popup) + ","
                            + jsString(complaint.getPriority().name()) + ","
                            + cluster.count() + ","
                            + jsString(complaint.getId()) + ","
                            + complaint.getResolutionConfirmationCount() + ","
                            + hasConfirmed + ","
                            + jsString(complaint.getStatus().name()) + ","
                            + ComplaintService.REQUIRED_RESOLUTION_CONFIRMATIONS
                            + ");"
            );
        }

        String heatPoints = filteredComplaints.stream()
                .map(Complaint::getLocation)
                .filter(location -> location != null
                        && Double.isFinite(location.getLatitude())
                        && Double.isFinite(location.getLongitude()))
                .map(location -> "[" + location.getLatitude() + ","
                        + location.getLongitude() + "]")
                .collect(Collectors.joining(","));
        engine.executeScript("setHeatPoints([" + heatPoints + "]);");
    }

    private java.util.List<Complaint> getFilteredComplaints(org.example.model.User user) {
        return ComplaintMapFilterService.filter(
                ComplaintService.getAllComplaints(), user, mineOnly, selectedPriority,
                selectedStatus, filterStartDate, filterEndDate);
    }

    public void showAreaSummary(double latitude, double longitude, double radiusMeters) {
        var visibleComplaints = getFilteredComplaints(UserSession.getLoggedUser());
        var nearbyComplaints = visibleComplaints.stream()
                .filter(complaint -> complaint.getLocation() != null)
                .filter(complaint -> ComplaintClusterService.distanceMeters(
                        latitude, longitude,
                        complaint.getLocation().getLatitude(),
                        complaint.getLocation().getLongitude()) <= radiusMeters)
                .toList();

        Map<String, Long> categories = nearbyComplaints.stream()
                .collect(Collectors.groupingBy(item -> item.getCategory().toString(),
                        Collectors.counting()));
        Map<String, Long> statuses = nearbyComplaints.stream()
                .collect(Collectors.groupingBy(item -> item.getStatus().toString(),
                        Collectors.counting()));
        String summary = "Resumo em um raio de " + (int) radiusMeters + " m"
                + "\n" + nearbyComplaints.size() + " reclamações"
                + "\n\nPor categoria\n" + formatBreakdown(categories)
                + "\n\nPor situação\n" + formatBreakdown(statuses);
        engine.executeScript("showAreaSummary(" + latitude + "," + longitude + ","
                + radiusMeters + "," + jsString(summary) + ");");
    }

    private String formatBreakdown(Map<String, Long> counts) {
        if (counts.isEmpty()) {
            return "Nenhuma ocorrência";
        }
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .map(entry -> "• " + entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("\n"));
    }

    public void confirmCommunityResolution(String complaintId) {
        Complaint complaint = ComplaintService.getAllComplaints().stream()
                .filter(item -> item.getId().equals(complaintId))
                .findFirst()
                .orElse(null);
        var result = ComplaintService.confirmCommunityResolution(
                complaint, UserSession.getLoggedUser());

        switch (result.status()) {
            case CONFIRMATION_RECORDED -> NotificationManager.info(
                    "Confirmação registrada (" + result.confirmations() + "/"
                            + ComplaintService.REQUIRED_RESOLUTION_CONFIRMATIONS + ").");
            case ALREADY_CONFIRMED -> NotificationManager.warning(
                    "Você já confirmou a resolução deste relato.");
            case RESOLVED -> NotificationManager.success(
                    "A comunidade confirmou a resolução. Relato concluído!");
            case ALREADY_RESOLVED -> NotificationManager.info(
                    "Este relato já está concluído.");
            case NOT_OPEN -> NotificationManager.warning(
                    "Relatos cancelados não podem receber confirmações.");
            case NOT_FOUND -> NotificationManager.error(
                    "Não foi possível localizar este relato.");
            case UNAUTHENTICATED -> NotificationManager.warning(
                    "Entre no sistema para confirmar a resolução.");
        }
        refreshComplaintMarkers();
    }

    private void refreshComplaintMarkers() {
        if (mode == MapMode.OVERVIEW
                && engine != null
                && engine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
            showComplaintMarkers();
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

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void showLoading() {
        engine.executeScript("showLoading(); setSelectionEnabled(false);");
    }

    public void hideLoading() {
        engine.executeScript("hideLoading();");
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
