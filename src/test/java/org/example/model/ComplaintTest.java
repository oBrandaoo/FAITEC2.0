package org.example.model;

import org.example.model.enums.ComplaintCategory;
import org.example.model.enums.ComplaintPriority;
import org.example.model.enums.ComplaintStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ComplaintTest {

    @Test
    void shouldCreateComplaintWithInitialHistoryAndCreator() {
        LocalDate registrationDate = LocalDate.of(2026, 8, 2);
        Complaint complaint = complaint(registrationDate);

        assertAll(
                () -> assertEquals(ComplaintCategory.BURACO_RUA, complaint.getCategory()),
                () -> assertEquals(ComplaintPriority.ALTA, complaint.getPriority()),
                () -> assertEquals(ComplaintStatus.PENDENTE, complaint.getStatus()),
                () -> assertEquals(registrationDate, complaint.getDate()),
                () -> assertEquals("USR-TEST", complaint.getCreatorId()),
                () -> assertEquals("Usuário de teste", complaint.getCreatorName()),
                () -> assertEquals(1, complaint.getHistory().size())
        );

        ComplaintHistoryEntry entry = complaint.getHistory().get(0);
        assertAll(
                () -> assertNull(entry.getPreviousStatus()),
                () -> assertEquals(ComplaintStatus.PENDENTE, entry.getNewStatus()),
                () -> assertEquals("Usuário de teste", entry.getResponsible()),
                () -> assertEquals(registrationDate.atStartOfDay(), entry.getChangedAt())
        );
    }

    @Test
    void shouldRegisterStatusChangeAndTrimNote() {
        Complaint complaint = complaint(LocalDate.now());

        complaint.changeStatus(
                ComplaintStatus.EM_ANALISE,
                "Atendente Municipal",
                "  Encaminhada para análise.  "
        );

        assertEquals(ComplaintStatus.EM_ANALISE, complaint.getStatus());
        assertEquals(2, complaint.getHistory().size());
        ComplaintHistoryEntry entry = complaint.getHistory().get(1);
        assertEquals(ComplaintStatus.PENDENTE, entry.getPreviousStatus());
        assertEquals(ComplaintStatus.EM_ANALISE, entry.getNewStatus());
        assertEquals("Encaminhada para análise.", entry.getNote());
    }

    @Test
    void shouldIgnoreNullOrRepeatedStatus() {
        Complaint complaint = complaint(LocalDate.now());

        complaint.changeStatus(null, "Sistema", "Sem alteração");
        complaint.changeStatus(ComplaintStatus.PENDENTE, "Sistema", "Sem alteração");

        assertEquals(ComplaintStatus.PENDENTE, complaint.getStatus());
        assertEquals(1, complaint.getHistory().size());
    }

    @Test
    void shouldUpdateDetailsAndAddHistoryNote() {
        Complaint complaint = complaint(LocalDate.now());
        Location newLocation = new Location(
                -22.251156,
                -45.702279,
                "Praça Delfim Moreira - Santa Rita do Sapucaí/MG"
        );

        boolean changed = complaint.updateDetails(
                ComplaintCategory.ILUMINACAO_PUBLICA,
                newLocation,
                "Poste apagado durante a noite.",
                ComplaintPriority.URGENTE,
                "Cidadão"
        );

        assertTrue(changed);
        assertAll(
                () -> assertEquals(ComplaintCategory.ILUMINACAO_PUBLICA, complaint.getCategory()),
                () -> assertEquals(newLocation, complaint.getLocation()),
                () -> assertEquals("Poste apagado durante a noite.", complaint.getDescription()),
                () -> assertEquals(ComplaintPriority.URGENTE, complaint.getPriority()),
                () -> assertEquals(2, complaint.getHistory().size()),
                () -> assertEquals("Dados da reclamação atualizados.", complaint.getHistory().get(1).getNote())
        );
    }

    @Test
    void shouldNotUpdateWhenDetailsAreEquivalent() {
        Complaint complaint = complaint(LocalDate.now());
        Location equivalentLocation = new Location(
                complaint.getLocation().getLatitude(),
                complaint.getLocation().getLongitude(),
                complaint.getLocation().getAddress()
        );

        boolean changed = complaint.updateDetails(
                complaint.getCategory(),
                equivalentLocation,
                complaint.getDescription(),
                complaint.getPriority(),
                "Cidadão"
        );

        assertFalse(changed);
        assertEquals(1, complaint.getHistory().size());
    }

    @Test
    void shouldKeepAttachmentsUniqueAndIgnoreInvalidPaths() {
        Complaint complaint = complaint(LocalDate.now());

        complaint.addAttachment(null);
        complaint.addAttachment("  ");
        complaint.addAttachment("foto-1.png");
        complaint.addAttachment("foto-1.png");
        complaint.addAttachment("foto-2.jpg");

        assertEquals(List.of("foto-1.png", "foto-2.jpg"), complaint.getAttachmentPaths());
        assertThrows(
                UnsupportedOperationException.class,
                () -> complaint.getAttachmentPaths().add("foto-3.png")
        );
    }

    @Test
    void shouldReplaceAndClearAttachments() {
        Complaint complaint = complaint(LocalDate.now());
        complaint.addAttachment("antiga.png");

        assertTrue(complaint.replaceAttachments(List.of("nova-1.png", "nova-2.jpg")));
        assertEquals(List.of("nova-1.png", "nova-2.jpg"), complaint.getAttachmentPaths());
        assertFalse(complaint.replaceAttachments(List.of("nova-1.png", "nova-2.jpg")));
        assertTrue(complaint.replaceAttachments(null));
        assertTrue(complaint.getAttachmentPaths().isEmpty());
    }

    @Test
    void historyCollectionShouldBeReadOnly() {
        Complaint complaint = complaint(LocalDate.now());

        assertThrows(
                UnsupportedOperationException.class,
                () -> complaint.getHistory().clear()
        );
    }

    private Complaint complaint(LocalDate date) {
        return new Complaint(
                ComplaintCategory.BURACO_RUA,
                new Location(
                        -22.252218,
                        -45.703128,
                        "Avenida Sinhá Moreira - Santa Rita do Sapucaí/MG"
                ),
                "Buraco próximo à faixa de pedestres.",
                ComplaintStatus.PENDENTE,
                ComplaintPriority.ALTA,
                date,
                "USR-TEST",
                "Usuário de teste"
        );
    }
}
