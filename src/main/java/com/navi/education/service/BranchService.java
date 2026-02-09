package com.navi.education.service;

import com.navi.education.dto.response.BranchFinancialSummary;
import com.navi.education.dto.response.BranchResponse;
import com.navi.education.exception.ResourceNotFoundException;
import com.navi.education.model.entity.Branch;
import com.navi.education.model.enums.BranchType;
import com.navi.education.model.enums.ExpenseType;
import com.navi.education.repository.AnnualCommitmentRepository;
import com.navi.education.repository.BranchRepository;
import com.navi.education.repository.ExpenseRepository;
import com.navi.education.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BranchService {

    private final BranchRepository branchRepository;
    private final ExpenseRepository expenseRepository;
    private final AnnualCommitmentRepository commitmentRepository;
    private final PaymentRepository paymentRepository;

    /**
     * Initialise les branches si elles n'existent pas
     */
    public void initializeBranches() {
        Arrays.stream(BranchType.values()).forEach(type -> {
            if (branchRepository.findByType(type).isEmpty()) {
                Branch branch = Branch.builder()
                        .type(type)
                        .nameFr(type.getFrenchName())
                        .nameAr(type.getArabicName())
                        .build();
                branchRepository.save(branch);
                log.info("Branche créée: {}", type.getFrenchName());
            }
        });
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::toBranchResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BranchResponse getBranchById(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branche", id));
        return toBranchResponse(branch);
    }

    @Transactional(readOnly = true)
    public BranchResponse getBranchByType(BranchType type) {
        Branch branch = branchRepository.findByType(type)
                .orElseThrow(() -> new ResourceNotFoundException("Branche avec type: " + type));
        return toBranchResponse(branch);
    }

    @Transactional(readOnly = true)
    public BranchFinancialSummary getBranchFinancialSummary(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branche", id));

        // Calculer les engagements et paiements
        BigDecimal totalCommitments = commitmentRepository.getTotalCommitmentsByBranch(id);
        BigDecimal totalPaymentsReceived = paymentRepository.getTotalPaymentsByBranch(id);
        BigDecimal remainingToReceive = (totalCommitments != null && totalPaymentsReceived != null)
                ? totalCommitments.subtract(totalPaymentsReceived)
                : (totalCommitments != null ? totalCommitments : BigDecimal.ZERO);

        // Calculer les dépenses par type
        BigDecimal totalFixedExpenses = expenseRepository.getTotalExpensesByBranchAndType(id, ExpenseType.FIXED);
        BigDecimal totalExtraExpenses = expenseRepository.getTotalExpensesByBranchAndType(id, ExpenseType.EXTRA);
        BigDecimal totalExpenses = BigDecimal.ZERO;
        if (totalFixedExpenses != null) {
            totalExpenses = totalExpenses.add(totalFixedExpenses);
        }
        if (totalExtraExpenses != null) {
            totalExpenses = totalExpenses.add(totalExtraExpenses);
        }

        // Calculer le bilan
        BigDecimal paymentsOrZero = totalPaymentsReceived != null ? totalPaymentsReceived : BigDecimal.ZERO;
        BigDecimal balance = paymentsOrZero.subtract(totalExpenses);

        // Statistiques
        long totalClassesCount = branchRepository.countActiveClassesByBranchId(id);
        Integer totalDonors = commitmentRepository.countUniqueDonorsByBranch(id);

        return BranchFinancialSummary.builder()
                .id(branch.getId())
                .type(branch.getType())
                .nameFr(branch.getNameFr())
                .nameAr(branch.getNameAr())
                .totalCommitments(totalCommitments != null ? totalCommitments : BigDecimal.ZERO)
                .totalPaymentsReceived(paymentsOrZero)
                .remainingToReceive(remainingToReceive)
                .totalFixedExpenses(totalFixedExpenses != null ? totalFixedExpenses : BigDecimal.ZERO)
                .totalExtraExpenses(totalExtraExpenses != null ? totalExtraExpenses : BigDecimal.ZERO)
                .totalExpenses(totalExpenses)
                .balance(balance)
                .totalClasses((int) totalClassesCount)
                .totalDonors(totalDonors != null ? totalDonors : 0)
                .build();
    }

    private BranchResponse toBranchResponse(Branch branch) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        BigDecimal monthlyExpenses = expenseRepository
                .getTotalExpensesByBranchAndPeriod(branch.getId(), startOfMonth, endOfMonth);

        return BranchResponse.builder()
                .id(branch.getId())
                .type(branch.getType())
                .nameFr(branch.getNameFr())
                .nameAr(branch.getNameAr())
                .description(branch.getDescription())
                .totalClasses((int) branchRepository.countActiveClassesByBranchId(branch.getId()))
                .monthlyExpenses(monthlyExpenses != null ? monthlyExpenses : BigDecimal.ZERO)
                .build();
    }
}
