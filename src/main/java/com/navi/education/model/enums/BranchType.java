package com.navi.education.model.enums;

public enum BranchType {
    DOUZ_DOUZ("Douz Douz", "دوز دوز"),
    LMILIKA("Lmilika", "فرع لمليكة"),
    AJOUIR("Ajouir", "فرع آجوير"),
    RABINA("Ar-Rabina", "فرع الربينة"),
    OULAD_AKHTIRA("Oulad Akhtira", "فرع أولاد اخطيرة"),
    IJGHMAJAK("Ijghmajak - Rihab Al-Quran", "فرع إجغماجك - رحاب القرآن"),
    BALGHARBAN_1("Balgharban 1", "فرع بالغربان 1"),
    BALGHARBAN_2("Balgharban 2", "فرع بالغربان 2"),
    BIR_AL_FATH("Bir Al-Fath", "فرع بير الفتح"),
    DAR_AL_SALAMA("Dar Al-Salama", "فصل دار السلامة"),
    ABU_BAKR_SIDDIQ("Abu Bakr Siddiq", "فصل أبو بكر الصديق"),
    AL_QALA("Al-Qala", "فصل القلعة"),
    SHEIKH_AHMAD("Sheikh Ahmad", "فصل الشيخ أحمد"),
    YANABI_HIDAYA("Yanabi Al-Hidaya Arkiz", "فرع ينابيع الهداية اركيز"),
    LKNILA("Lknila", "فرع لكنيلة"),
    DAR_AL_KHEIR("Dar Al-Kheir", "فرع دار الخير");

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
