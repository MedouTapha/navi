package com.navi.education.service;

import com.navi.education.dto.response.MonthlyReportBranchDTO;
import com.navi.education.dto.response.MonthlyReportDTO;
import com.navi.education.dto.response.MonthlyReportLineDTO;
import com.navi.education.model.entity.Branch;
import com.navi.education.model.entity.Expense;
import com.navi.education.model.enums.ExpenseType;
import com.navi.education.repository.BranchRepository;
import com.navi.education.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MonthlyReportService {

    private static final Map<Integer, String> ARABIC_MONTHS = Map.ofEntries(
            Map.entry(1, "يناير"),
            Map.entry(2, "فبراير"),
            Map.entry(3, "مارس"),
            Map.entry(4, "أبريل"),
            Map.entry(5, "مايو"),
            Map.entry(6, "يونيو"),
            Map.entry(7, "يوليو"),
            Map.entry(8, "أغسطس"),
            Map.entry(9, "سبتمبر"),
            Map.entry(10, "أكتوبر"),
            Map.entry(11, "نوفمبر"),
            Map.entry(12, "ديسمبر")
    );

    private final BranchRepository branchRepository;
    private final ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public MonthlyReportDTO getMonthlyReport(int year, int month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<Branch> branches = branchRepository.findAll();
        List<MonthlyReportBranchDTO> branchDTOs = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Branch branch : branches) {
            List<Expense> expenses = expenseRepository.findByBranchAndPeriod(
                    branch.getId(), startOfMonth, endOfMonth);

            if (expenses.isEmpty()) continue;

            List<MonthlyReportLineDTO> lines = new ArrayList<>();
            BigDecimal branchTotal = BigDecimal.ZERO;

            for (Expense expense : expenses) {
                String label = expense.getExpenseType() == ExpenseType.FIXED
                        ? expense.getEducationClass().getNameAr()
                        : expense.getDescription();

                lines.add(MonthlyReportLineDTO.builder()
                        .label(label)
                        .amount(expense.getAmount())
                        .extra(expense.getExpenseType() == ExpenseType.EXTRA)
                        .build());

                branchTotal = branchTotal.add(expense.getAmount());
            }

            branchDTOs.add(MonthlyReportBranchDTO.builder()
                    .branchNameAr(branch.getNameAr())
                    .lines(lines)
                    .total(branchTotal)
                    .build());

            grandTotal = grandTotal.add(branchTotal);
        }

        return MonthlyReportDTO.builder()
                .month(month)
                .year(year)
                .monthNameAr(ARABIC_MONTHS.getOrDefault(month, String.valueOf(month)))
                .branches(branchDTOs)
                .grandTotal(grandTotal)
                .build();
    }
}
