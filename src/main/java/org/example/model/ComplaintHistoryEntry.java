package org.example.model;

import java.time.LocalDateTime;

import org.example.model.enums.ComplaintStatus;

public class ComplaintHistoryEntry {
    private final LocalDateTime changedAt;
    private final ComplaintStatus previousStatus;
    private final ComplaintStatus newStatus;
    private final String responsible;
    private final String note;

    public ComplaintHistoryEntry(LocalDateTime changedAt, ComplaintStatus previousStatus,
        ComplaintStatus newStatus, String responsible, String note) {
        this.changedAt = changedAt;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.responsible = responsible;
        this.note = note;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public ComplaintStatus getPreviousStatus() {
        return previousStatus;
    }

    public ComplaintStatus getNewStatus() {
        return newStatus;
    }

    public String getResponsible() {
        return responsible;
    }

    public String getNote() {
        return note;
    }
}
