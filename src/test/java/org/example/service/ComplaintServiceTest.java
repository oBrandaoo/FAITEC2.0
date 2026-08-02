package org.example.service;

import org.example.model.Complaint;
import org.example.model.Location;
import org.example.model.User;
import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.example.model.enums.UserRole;
import org.example.model.enums.UserStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComplaintServiceTest {

    private Complaint addedComplaint;

    @AfterEach
    void removeComplaintAddedByTest() {
        if (addedComplaint != null) {
            ComplaintService.getAllComplaints().remove(addedComplaint);
        }
    }

    @Test
    void shouldLoadDemonstrationComplaintsFromSantaRitaDoSapucai() {
        List<Complaint> complaints = ComplaintService.getAllComplaints();

        assertFalse(complaints.isEmpty());
        assertTrue(complaints.stream().allMatch(item ->
                item.getLocation().getAddress().contains("Santa Rita do Sapucaí")
        ));
        assertTrue(complaints.stream().anyMatch(item ->
                item.getPriority() == ComplaintPriority.URGENTE
        ));
        assertTrue(complaints.stream().anyMatch(item ->
                item.getStatus() == ComplaintStatus.RESOLVIDO
        ));
    }

    @Test
    void managerShouldSeeEveryComplaint() {
        User administrator = user("USR-001", UserRole.ADMINISTRADOR);
        User attendant = user("USR-002", UserRole.ATENDENTE);

        assertEquals(
                ComplaintService.getAllComplaints().size(),
                ComplaintService.getComplaintsFor(administrator).size()
        );
        assertEquals(
                ComplaintService.getAllComplaints().size(),
                ComplaintService.getComplaintsFor(attendant).size()
        );
    }

    @Test
    void citizenShouldSeeOnlyOwnComplaints() {
        User citizen = user("USR-003", UserRole.CIDADAO);

        List<Complaint> visible = ComplaintService.getComplaintsFor(citizen);

        assertFalse(visible.isEmpty());
        assertTrue(visible.stream().allMatch(item ->
                citizen.getId().equals(item.getCreatorId())
        ));
        assertTrue(visible.size() < ComplaintService.getAllComplaints().size());
    }

    @Test
    void shouldReturnEmptyListWithoutAuthenticatedUser() {
        assertTrue(ComplaintService.getComplaintsFor(null).isEmpty());
    }

    @Test
    void shouldAddComplaintToInMemoryCollection() {
        int initialSize = ComplaintService.getAllComplaints().size();
        addedComplaint = complaint();

        ComplaintService.addComplaint(addedComplaint);

        assertEquals(initialSize + 1, ComplaintService.getAllComplaints().size());
        assertTrue(ComplaintService.getAllComplaints().contains(addedComplaint));
    }

    @Test
    void shouldUpdateStatusUsingResponsibleUserName() {
        Complaint complaint = complaint();
        User attendant = user("USR-002", UserRole.ATENDENTE);

        ComplaintService.updateStatus(
                complaint,
                ComplaintStatus.EM_ANALISE,
                attendant,
                "Triagem concluída."
        );

        assertEquals(ComplaintStatus.EM_ANALISE, complaint.getStatus());
        assertEquals(2, complaint.getHistory().size());
        assertEquals("Atendente de teste", complaint.getHistory().get(1).getResponsible());
        assertEquals("Triagem concluída.", complaint.getHistory().get(1).getNote());
    }

    @Test
    void statusUpdateShouldAcceptSystemAsResponsible() {
        Complaint complaint = complaint();

        ComplaintService.updateStatus(
                complaint,
                ComplaintStatus.EM_EXECUCAO,
                null,
                null
        );

        assertEquals("Sistema", complaint.getHistory().get(1).getResponsible());
        assertEquals("", complaint.getHistory().get(1).getNote());
    }

    @Test
    void invalidStatusUpdateShouldNotThrowOrChangeComplaint() {
        Complaint complaint = complaint();

        assertDoesNotThrow(() -> ComplaintService.updateStatus(null, ComplaintStatus.RESOLVIDO, null, ""));
        assertDoesNotThrow(() -> ComplaintService.updateStatus(complaint, null, null, ""));
        assertEquals(ComplaintStatus.PENDENTE, complaint.getStatus());
        assertEquals(1, complaint.getHistory().size());
    }

    private User user(String id, UserRole role) {
        String name = role == UserRole.ATENDENTE ? "Atendente de teste" : "Usuário de teste";
        return new User(id, name, "1234", UserStatus.ATIVA, role);
    }

    private Complaint complaint() {
        return new Complaint(
                ComplaintCategory.ESGOTO,
                new Location(-22.25, -45.70, "Santa Rita do Sapucaí/MG"),
                "Reclamação criada pelo teste.",
                ComplaintStatus.PENDENTE,
                ComplaintPriority.MEDIA,
                LocalDate.now(),
                "USR-TEST",
                "Usuário de teste"
        );
    }
}
