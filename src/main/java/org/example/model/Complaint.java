package org.example.model;

import org.example.model.enums.ComplaintCategory;

import java.time.LocalDate;

public class Complaint {
    private ComplaintCategory category;
    private String location;
    private String description;
    private String status;
    private LocalDate date;

    public Complaint(ComplaintCategory category, String location, String description) {
        this.category = category;
        this.location = location;
        this.description = description;
        this.status = "PENDENTE";
        this.date = LocalDate.now();
    }

    public ComplaintCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }
}
