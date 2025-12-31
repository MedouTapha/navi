package com.navi.education.model.enums;

public enum ExpenseType {
    FIXED("Dépense Fixe Mensuelle"),
    EXTRA("Dépense Exceptionnelle");

    private final String displayName;

    ExpenseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
