package org.example.model;

import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintStatus;

import java.time.LocalDate;

public class Complaint {
    private ComplaintCategory category;
    private Location location;
    private String description;
    private ComplaintStatus status;
    private LocalDate date;

    public Complaint(ComplaintCategory category, Location location, String description, ComplaintStatus status) {
        this.category = category;
        this.location = location;
        this.description = description;
        this.status = status;
        this.date = LocalDate.now();
    }

    public void setCategory(ComplaintCategory category) {
        this.category = category;
    }

    public void setStatus(ComplaintStatus status) {
        this.status = status;
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

    public LocalDate getDate() {
        return date;
    }
}
