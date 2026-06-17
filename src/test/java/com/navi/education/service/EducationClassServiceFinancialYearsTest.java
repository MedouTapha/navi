package com.navi.education.service;

import com.navi.education.dto.response.ClassFinancialYearSummary;
import com.navi.education.model.entity.EducationClass;
import com.navi.education.repository.EducationClassRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Vérifie la cohérence de l'historique par année financière calculé par le service.
 */
@SpringBootTest
class EducationClassServiceFinancialYearsTest {

    @Autowired
    private EducationClassService classService;

    @Autowired
    private EducationClassRepository classRepository;

    @Test
    void financialYearsHistory_isOrderedDescendingAndConsistent() {
        EducationClass cls = classRepository.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getActive()))
                .findFirst()
                .orElseThrow();

        List<ClassFinancialYearSummary> history = classService.getFinancialYearsHistory(cls.getId());

        assertFalse(history.isEmpty(), "Une classe active doit avoir au moins une année financière");

        // Ordre décroissant (année la plus récente en premier)
        for (int i = 1; i < history.size(); i++) {
            assertTrue(history.get(i - 1).getFinancialYear() >= history.get(i).getFinancialYear(),
                    "L'historique doit être trié par année décroissante");
        }

        // Exactement une année marquée "courante"
        long currentCount = history.stream().filter(ClassFinancialYearSummary::isCurrent).count();
        assertEquals(1, currentCount, "Une seule année doit être marquée comme courante");

        // Cohérence des totaux pour chaque année
        for (ClassFinancialYearSummary y : history) {
            assertEquals(0, y.getTotalExpenses().compareTo(
                    y.getFixedExpenses().add(y.getExtraExpenses())),
                    "totalExpenses = fixed + extra");
            assertEquals(0, y.getTotalReceived().compareTo(
                    y.getPaidDonations().add(y.getExtraDonations())),
                    "totalReceived = paid + extra donations");
            assertEquals(0, y.getBalance().compareTo(
                    y.getTotalReceived().subtract(y.getTotalExpenses())),
                    "balance = received - expenses");
            assertNotNull(y.getPeriodStart());
            assertNotNull(y.getPeriodEnd());
            assertTrue(y.getPeriodEnd().isAfter(y.getPeriodStart()),
                    "La fin de période doit suivre le début");
        }
    }
}
