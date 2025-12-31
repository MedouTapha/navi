package com.navi.education.service;

import com.navi.education.model.entity.EducationClass;
import com.navi.education.repository.EducationClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledExpenseService {

    private final EducationClassRepository classRepository;
    private final ExpenseService expenseService;

    /**
     * Génère automatiquement les dépenses mensuelles fixes le 1er de chaque mois à 00:01
     * Cron: seconde minute heure jour mois jour_de_semaine
     */
    @Scheduled(cron = "0 1 0 1 * *") // Exécute le 1er de chaque mois à 00:01
    @Transactional
    public void generateMonthlyExpenses() {
        log.info("Début de la génération automatique des dépenses mensuelles");

        LocalDate today = LocalDate.now();
        List<EducationClass> activeClasses = classRepository.findByActiveTrue();

        int successCount = 0;
        int skipCount = 0;

        for (EducationClass educationClass : activeClasses) {
            // Vérifier que la classe a démarré
            if (educationClass.getStartDate().isAfter(today)) {
                log.debug("Classe {} non démarrée, ignorée", educationClass.getNameFr());
                skipCount++;
                continue;
            }

            // Vérifier que la classe n'est pas terminée
            if (educationClass.getEndDate() != null && educationClass.getEndDate().isBefore(today)) {
                log.debug("Classe {} terminée, ignorée", educationClass.getNameFr());
                skipCount++;
                continue;
            }

            try {
                expenseService.createMonthlyFixedExpense(educationClass.getId(), today);
                successCount++;
            } catch (Exception e) {
                log.error("Erreur lors de la création de la dépense mensuelle pour la classe {}: {}",
                        educationClass.getNameFr(), e.getMessage());
            }
        }

        log.info("Génération des dépenses mensuelles terminée. Succès: {}, Ignorées: {}, Total: {}",
                successCount, skipCount, activeClasses.size());
    }

    /**
     * Méthode pour générer manuellement les dépenses mensuelles (utile pour les tests)
     */
    @Transactional
    public void generateMonthlyExpensesManually() {
        log.info("Génération manuelle des dépenses mensuelles déclenchée");
        generateMonthlyExpenses();
    }
}
