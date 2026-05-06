package com.oop.Model;

public enum MembershipType {
    FREE,
    PREMIUM;

    public static MembershipType fromLabel(String label) {
        if (label == null) {
            return FREE;
        }
        String normalized = label.trim().toLowerCase();
        if (normalized.contains("premium")) {
            return PREMIUM;
        }
        return FREE;
    }
}
