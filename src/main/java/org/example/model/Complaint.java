package org.example.model;

import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Complaint {
    private ComplaintCategory category;
    private Location location;
    private String description;
    private ComplaintStatus status;
    private ComplaintPriority priority;
    private LocalDate date;
    private final String creatorId;
    private final String creatorName;
    private final List<String> attachmentPaths = new ArrayList<>();
    private final List<ComplaintHistoryEntry> history = new ArrayList<>();

    public Complaint(ComplaintCategory category, Location location, String description, ComplaintStatus status) {
        this(category, location, description, status, ComplaintPriority.MEDIA, LocalDate.now());
    }

    public Complaint(
            ComplaintCategory category,
            Location location,
            String description,
            ComplaintStatus status,
            LocalDate date
    ) {
        this(category, location, description, status, ComplaintPriority.MEDIA, date);
    }

    public Complaint(
            ComplaintCategory category,
            Location location,
            String description,
            ComplaintStatus status,
            ComplaintPriority priority,
            LocalDate date
    ) {
        this(category, location, description, status, priority, date, "SYSTEM", "Sistema");
    }

    public Complaint(
            ComplaintCategory category,
            Location location,
            String description,
            ComplaintStatus status,
            ComplaintPriority priority,
            LocalDate date,
            String registeredBy
    ) {
        this(category, location, description, status, priority, date, "SYSTEM", registeredBy);
    }

    public Complaint(
            ComplaintCategory category,
            Location location,
            String description,
            ComplaintStatus status,
            ComplaintPriority priority,
            LocalDate date,
            String creatorId,
            String creatorName
    ) {
        this.category = category;
        this.location = location;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.date = date;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        history.add(new ComplaintHistoryEntry(
                date.atStartOfDay(),
                null,
                status,
                creatorName,
                "Reclamação registrada."
        ));
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public void changeStatus(
            ComplaintStatus newStatus,
            String responsible,
            String note
    ) {
        if (newStatus == null || newStatus == status) {
            return;
        }

        ComplaintStatus previousStatus = status;
        status = newStatus;
        history.add(new ComplaintHistoryEntry(
                LocalDateTime.now(),
                previousStatus,
                newStatus,
                responsible,
                note == null ? "" : note.trim()
        ));
    }

    public boolean updateDetails(
            ComplaintCategory newCategory,
            Location newLocation,
            String newDescription,
            ComplaintPriority newPriority,
            String responsible
    ) {
        boolean changed = category != newCategory
                || priority != newPriority
                || !Objects.equals(description, newDescription)
                || !sameLocation(location, newLocation);
        if (!changed) {
            return false;
        }

        category = newCategory;
        location = newLocation;
        description = newDescription;
        priority = newPriority;
        addHistoryNote(responsible, "Dados da reclamação atualizados.");
        return true;
    }

    public void addHistoryNote(String responsible, String note) {
        history.add(new ComplaintHistoryEntry(
                LocalDateTime.now(),
                status,
                status,
                responsible,
                note
        ));
    }

    private boolean sameLocation(Location first, Location second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }
        return Double.compare(first.getLatitude(), second.getLatitude()) == 0
                && Double.compare(first.getLongitude(), second.getLongitude()) == 0
                && Objects.equals(first.getAddress(), second.getAddress());
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Location getLocation() {
        return location;
    }

    public ComplaintStatus getStatus() {
        return status;
    }

    public ComplaintPriority getPriority() {
        return priority;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public List<ComplaintHistoryEntry> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void addAttachment(String path) {
        if (path != null && !path.isBlank() && !attachmentPaths.contains(path)) {
            attachmentPaths.add(path);
        }
    }

    public List<String> getAttachmentPaths() {
        return Collections.unmodifiableList(attachmentPaths);
    }

    public boolean replaceAttachments(List<String> paths) {
        List<String> normalized = paths == null ? List.of() : paths;
        if (attachmentPaths.equals(normalized)) {
            return false;
        }
        attachmentPaths.clear();
        attachmentPaths.addAll(normalized);
        return true;
    }
}
