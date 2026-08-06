package org.example.model.enums;

public enum UserRole {
    ADMINISTRADOR("Administrador", true, true, true),
    ATENDENTE("Atendente", false, true, true),
    CIDADAO("Cidadão", true, false, true);

    private final String displayName;
    private final boolean canCreateComplaint;
    private final boolean canManageComplaints;
    private final boolean canViewMap;

    UserRole(
            String displayName,
            boolean canCreateComplaint,
            boolean canManageComplaints,
            boolean canViewMap
    ) {
        this.displayName = displayName;
        this.canCreateComplaint = canCreateComplaint;
        this.canManageComplaints = canManageComplaints;
        this.canViewMap = canViewMap;
    }

    public boolean canCreateComplaint() {
        return canCreateComplaint;
    }

    public boolean canManageComplaints() {
        return canManageComplaints;
    }

    public boolean canViewMap() {
        return canViewMap;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
