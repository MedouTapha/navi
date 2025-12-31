package com.navi.education.model.enums;

public enum BranchType {
    NOUAKCHOTT("Nouakchott", "نواكشوط"),
    NOUADHIBOU("Nouadhibou", "نواذيبو"),
    ROSSO("Rosso", "روصو"),
    KSAR("Ksar", "كصر");

    private final String frenchName;
    private final String arabicName;

    BranchType(String frenchName, String arabicName) {
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
