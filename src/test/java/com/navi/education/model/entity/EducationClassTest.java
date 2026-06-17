package com.navi.education.model.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EducationClassTest {

    private EducationClass classStartingJan2025() {
        return EducationClass.builder()
                .startDate(LocalDate.of(2025, 1, 1))
                .monthlyFixedAmount(new BigDecimal("100000"))
                .build();
    }

    @Test
    void financialYear_beforeStart_returnsZero() {
        EducationClass c = classStartingJan2025();
        assertEquals(0, c.getFinancialYear(LocalDate.of(2024, 12, 31)));
    }

    @Test
    void financialYear_sameYear_returnsStartYear() {
        EducationClass c = classStartingJan2025();
        assertEquals(2025, c.getFinancialYear(LocalDate.of(2025, 1, 1)));
        assertEquals(2025, c.getFinancialYear(LocalDate.of(2025, 6, 15)));
        assertEquals(2025, c.getFinancialYear(LocalDate.of(2025, 12, 31)));
    }

    @Test
    void financialYear_nextYear_returnsNextYear() {
        EducationClass c = classStartingJan2025();
        assertEquals(2026, c.getFinancialYear(LocalDate.of(2026, 1, 1)));
        assertEquals(2026, c.getFinancialYear(LocalDate.of(2026, 12, 31)));
    }

    @Test
    void financialYearStart_and_end_areConsistent() {
        EducationClass c = classStartingJan2025();
        assertEquals(LocalDate.of(2026, 1, 1), c.getFinancialYearStart(2026));
        assertEquals(LocalDate.of(2025, 12, 31), c.getFinancialYearEnd(2025));
    }
}
