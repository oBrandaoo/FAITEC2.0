package org.example.model;

import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Complaint {
    private ComplaintCategory category;
    private Location location;
    private String description;
    private ComplaintStatus status;
    private ComplaintPriority priority;
    private LocalDate date;
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
        this(category, location, description, status, priority, date, "Sistema");
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
        this.category = category;
        this.location = location;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.date = date;
        history.add(new ComplaintHistoryEntry(
                date.atStartOfDay(),
                null,
                status,
                registeredBy,
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

    public List<ComplaintHistoryEntry> getHistory() {
        return Collections.unmodifiableList(history);
    }
}
