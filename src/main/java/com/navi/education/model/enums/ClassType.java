package com.navi.education.model.enums;

public enum ClassType {
    RECITATION("Récitation", "تحفيظ"),
    PEDAGOGICAL("Pédagogique", "تعليمي");

    private final String frenchName;
    private final String arabicName;

    ClassType(String frenchName, String arabicName) {
        this.frenchName = frenchName;
        this.arabicName = arabicName;
    }

    public String getFrenchName() {
        return frenchName;
    }

    public String getArabicName() {
        return arabicName;
    }
}
