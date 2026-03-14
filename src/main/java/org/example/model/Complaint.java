package org.example.model;

public class Complaint {
    private String category;
    private String description;
    private String location;

    public Complaint(String category, String description, String location) {
        this.category = category;
        this.description = description;
        this.location = location;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }
}
