package com.navi.education.model.enums;

public enum PaymentMethod {
    BANKILY("Bankily"),
    SEDAD("Sedad"),
    MASRVI("Masrvi"),
    CASH("Cash");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
