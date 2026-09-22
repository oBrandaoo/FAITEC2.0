package org.example.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintSubcategory;

/** Groups reports of the same classified issue when their locations are nearby. */
public final class ComplaintClusterService {

    public static final double CLUSTER_RADIUS_METERS = 40.0;
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private ComplaintClusterService() {
    }

    public static List<ComplaintCluster> groupByProximity(List<Complaint> complaints) {
        if (complaints == null || complaints.isEmpty()) {
            return List.of();
        }

        List<MutableCluster> clusters = new ArrayList<>();
        for (Complaint complaint : complaints) {
            if (complaint == null) {
                continue;
            }

            Location location = complaint.getLocation();
            MutableCluster closestCluster = null;
            double closestDistance = Double.MAX_VALUE;

            if (hasCoordinates(location)) {
                for (MutableCluster cluster : clusters) {
                    if (!cluster.matchesIssue(complaint)) {
                        continue;
                    }

                    double distance = distanceMeters(
                        cluster.anchorLatitude,
                        cluster.anchorLongitude,
                        location.getLatitude(),
                        location.getLongitude()
                    );
                    if (distance <= CLUSTER_RADIUS_METERS && distance < closestDistance) {
                        closestCluster = cluster;
                        closestDistance = distance;
                    }
                }
            }

            if (closestCluster == null) {
                clusters.add(new MutableCluster(complaint));
            } else {
                closestCluster.add(complaint);
            }
        }

        return clusters.stream().map(MutableCluster::toCluster).toList();
    }

    public static double distanceMeters(double latitude1, double longitude1,
            double latitude2, double longitude2) {
        double latitudeDelta = Math.toRadians(latitude2 - latitude1);
        double longitudeDelta = Math.toRadians(longitude2 - longitude1);
        double firstLatitude = Math.toRadians(latitude1);
        double secondLatitude = Math.toRadians(latitude2);

        double haversine = Math.pow(Math.sin(latitudeDelta / 2), 2)
            + Math.cos(firstLatitude) * Math.cos(secondLatitude)
            * Math.pow(Math.sin(longitudeDelta / 2), 2);
        double centralAngle = 2 * Math.atan2(
            Math.sqrt(Math.min(1.0, haversine)),
            Math.sqrt(Math.max(0.0, 1.0 - haversine))
        );
        return EARTH_RADIUS_METERS * centralAngle;
    }

    private static boolean hasCoordinates(Location location) {
        return location != null
            && Double.isFinite(location.getLatitude())
            && Double.isFinite(location.getLongitude());
    }

    public record ComplaintCluster(List<Complaint> complaints, double latitude, double longitude) {
        public ComplaintCluster {
            complaints = List.copyOf(complaints);
        }

        public int count() {
            return complaints.size();
        }

        public Complaint highestPriorityComplaint() {
            return complaints.stream()
                .max((first, second) -> Integer.compare(
                    priorityRank(first), priorityRank(second)))
                .orElseThrow();
        }

        private static int priorityRank(Complaint complaint) {
            return complaint.getPriority() == null ? -1 : complaint.getPriority().ordinal();
        }
    }

    private static final class MutableCluster {
        private final ComplaintCategory category;
        private final ComplaintSubcategory subcategory;
        private final double anchorLatitude;
        private final double anchorLongitude;
        private final List<Complaint> complaints = new ArrayList<>();

        private MutableCluster(Complaint first) {
            category = first.getCategory();
            subcategory = first.getSubcategory();
            Location location = first.getLocation();
            anchorLatitude = location == null ? Double.NaN : location.getLatitude();
            anchorLongitude = location == null ? Double.NaN : location.getLongitude();
            add(first);
        }

        private boolean matchesIssue(Complaint complaint) {
            return Objects.equals(category, complaint.getCategory())
                && Objects.equals(subcategory, complaint.getSubcategory());
        }

        private void add(Complaint complaint) {
            complaints.add(complaint);
        }

        private ComplaintCluster toCluster() {
            return new ComplaintCluster(complaints, anchorLatitude, anchorLongitude);
        }
    }
}
