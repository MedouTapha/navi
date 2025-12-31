package com.navi.education.service;

import com.navi.education.dto.request.ExpenseRequest;
import com.navi.education.dto.response.ExpenseResponse;
import com.navi.education.exception.ResourceNotFoundException;
import com.navi.education.model.entity.EducationClass;
import com.navi.education.model.entity.Expense;
import com.navi.education.model.enums.ExpenseType;
import com.navi.education.repository.EducationClassRepository;
import com.navi.education.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final EducationClassRepository classRepository;

    /**
     * Ajoute une dépense exceptionnelle
     */
    public ExpenseResponse createExtraExpense(ExpenseRequest request) {
        EducationClass educationClass = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new ResourceNotFoundException("Classe", request.getClassId()));

        Expense expense = Expense.builder()
                .educationClass(educationClass)
                .expenseType(ExpenseType.EXTRA)
                .amount(request.getAmount())
                .expenseDate(request.getExpenseDate())
                .description(request.getDescription())
                .build();

        expense = expenseRepository.save(expense);
        log.info("Dépense exceptionnelle créée pour la classe {}: {} MRU",
                educationClass.getNameFr(), expense.getAmount());

        return toExpenseResponse(expense);
    }

    /**
     * Crée une dépense fixe mensuelle (utilisée par le scheduler)
     */
    public ExpenseResponse createMonthlyFixedExpense(Long classId, LocalDate expenseDate) {
        EducationClass educationClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Classe", classId));

        // Vérifie qu'une dépense fixe n'existe pas déjà pour ce mois
        LocalDate startOfMonth = expenseDate.withDayOfMonth(1);
        LocalDate endOfMonth = expenseDate.withDayOfMonth(expenseDate.lengthOfMonth());

        boolean exists = expenseRepository.existsByEducationClassIdAndExpenseTypeAndExpenseDateBetween(
                classId, ExpenseType.FIXED, startOfMonth, endOfMonth);

        if (exists) {
            log.warn("Dépense fixe déjà existante pour la classe {} pour le mois {}/{}",
                    educationClass.getNameFr(), expenseDate.getMonthValue(), expenseDate.getYear());
            return null;
        }

        Expense expense = Expense.builder()
                .educationClass(educationClass)
                .expenseType(ExpenseType.FIXED)
                .amount(educationClass.getMonthlyFixedAmount())
                .expenseDate(expenseDate)
                .description("Dépense mensuelle fixe")
                .build();

        expense = expenseRepository.save(expense);
        log.info("Dépense fixe mensuelle créée pour la classe {}: {} MRU",
                educationClass.getNameFr(), expense.getAmount());

        return toExpenseResponse(expense);
    }

    /**
     * Supprime une dépense exceptionnelle
     */
    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dépense", id));

        if (expense.getExpenseType() == ExpenseType.FIXED) {
            log.warn("Tentative de suppression d'une dépense fixe: {}", id);
            throw new IllegalArgumentException("Les dépenses fixes ne peuvent pas être supprimées manuellement");
        }

        expenseRepository.delete(expense);
        log.info("Dépense exceptionnelle supprimée: {}", id);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByClass(Long classId) {
        return expenseRepository.findByEducationClassId(classId).stream()
                .map(this::toExpenseResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByClassAndType(Long classId, ExpenseType type) {
        return expenseRepository.findByEducationClassIdAndExpenseType(classId, type).stream()
                .map(this::toExpenseResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByClassAndYear(Long classId, Integer financialYear) {
        return expenseRepository.findByEducationClassIdAndFinancialYear(classId, financialYear).stream()
                .map(this::toExpenseResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByClassAndPeriod(Long classId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findExpensesByClassAndPeriod(classId, startDate, endDate).stream()
                .map(this::toExpenseResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dépense", id));
        return toExpenseResponse(expense);
    }

    private ExpenseResponse toExpenseResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .classId(expense.getEducationClass().getId())
                .className(expense.getEducationClass().getNameFr())
                .expenseType(expense.getExpenseType())
                .amount(expense.getAmount())
                .expenseDate(expense.getExpenseDate())
                .description(expense.getDescription())
                .financialYear(expense.getFinancialYear())
                .month(expense.getMonth())
                .build();
    }
}
