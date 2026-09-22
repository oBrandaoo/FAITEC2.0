package org.example.service;

import java.util.List;
import java.util.Objects;

import org.example.model.Complaint;
import org.example.model.User;
import org.example.model.enums.ComplaintPriority;

/** Applies the map's owner and priority filters before reports are grouped. */
public final class ComplaintMapFilterService {

    private ComplaintMapFilterService() {
    }

    public static List<Complaint> filter(List<Complaint> complaints, User user,
            boolean mineOnly, ComplaintPriority priority) {
        if (complaints == null || complaints.isEmpty()) {
            return List.of();
        }

        return complaints.stream()
            .filter(Objects::nonNull)
            .filter(complaint -> !mineOnly || (user != null
                && Objects.equals(user.getId(), complaint.getCreatorId())))
            .filter(complaint -> priority == null || complaint.getPriority() == priority)
            .toList();
    }
}
