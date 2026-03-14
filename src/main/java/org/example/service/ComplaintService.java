package org.example.service;

import org.example.model.Complaint;

import java.util.ArrayList;
import java.util.List;

public class ComplaintService {

    private static List<Complaint> complaints = new ArrayList<>();

    public static void addComplaint(Complaint complaint) {
        complaints.add(complaint);
    }

    public static List<Complaint> getAllComplaints() {
        return complaints;
    }
}
