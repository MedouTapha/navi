package com.navi.education.service;

import com.navi.education.dto.response.ClassDashboardSummary;
import com.navi.education.dto.response.DashboardBranchSummary;
import com.navi.education.dto.response.DashboardSummary;
import com.navi.education.model.entity.Branch;
import com.navi.education.model.entity.EducationClass;
import com.navi.education.model.enums.ExpenseType;
import com.navi.education.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final BranchRepository branchRepository;
    private final EducationClassRepository classRepository;
    private final ExpenseRepository expenseRepository;
    private final PaymentRepository paymentRepository;
    private final AnnualCommitmentRepository commitmentRepository;
    private final ExtraDonationRepository extraDonationRepository;

    /**
     * Calcule le bilan de l'institut à une date donnée.
     * Chaque classe utilise sa propre année financière en cours à cette date.
     */
    @Transactional(readOnly = true)
    public DashboardSummary getDashboardSummary(LocalDate referenceDate) {
        List<Branch> allBranches = branchRepository.findAll();
        List<DashboardBranchSummary> branchSummaries = new ArrayList<>();

        BigDecimal instFixed = ZERO, instExtra = ZERO;
        BigDecimal instCommitted = ZERO, instPaid = ZERO, instExtraDon = ZERO;

        for (Branch branch : allBranches) {
            DashboardBranchSummary branchSummary = calculateBranchSummary(branch, referenceDate);
            if (branchSummary.getClasses().isEmpty()) continue;
            branchSummaries.add(branchSummary);

            instFixed    = instFixed.add(branchSummary.getAnnualFixedExpenses());
            instExtra    = instExtra.add(branchSummary.getAnnualExtraExpenses());
            instCommitted = instCommitted.add(branchSummary.getTotalCommittedDonations());
            instPaid      = instPaid.add(branchSummary.getAnnualDonationsPaid());
            instExtraDon  = instExtraDon.add(branchSummary.getTotalExtraDonations());
        }

        BigDecimal instTotalExpenses = instFixed.add(instExtra);
        BigDecimal instTotalReceived = instPaid.add(instExtraDon);
        BigDecimal instLate = instCommitted.subtract(instPaid);
        BigDecimal globalBalance = instTotalReceived.subtract(instTotalExpenses);

        long totalClasses = classRepository.findByActiveTrue().size();

        return DashboardSummary.builder()
                .referenceDate(referenceDate)
                .branches(branchSummaries)
                .totalFixedExpenses(instFixed)
                .totalExtraExpenses(instExtra)
                .totalExpenses(instTotalExpenses)
                .totalCommittedDonations(instCommitted)
                .totalDonationsPaid(instPaid)
                .totalDonationsLate(instLate.max(ZERO))
                .totalExtraDonations(instExtraDon)
                .totalReceived(instTotalReceived)
                .globalBalance(globalBalance)
                .totalBranches(branchSummaries.size())
                .totalClasses((int) totalClasses)
                .totalDonors(countUniqueDonors())
                .build();
    }

    /**
     * Calcule le bilan d'une seule branche à une date donnée (pour la vue détail).
     */
    @Transactional(readOnly = true)
    public DashboardBranchSummary getBranchClassSummaries(Long branchId, LocalDate referenceDate) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branche non trouvée: " + branchId));
        return calculateBranchSummary(branch, referenceDate);
    }

    // ─────────────────────────────────────────────────────────────
    // Calculs internes
    // ─────────────────────────────────────────────────────────────

    private DashboardBranchSummary calculateBranchSummary(Branch branch, LocalDate referenceDate) {
        List<EducationClass> classes = classRepository.findActiveClassesByBranch(branch.getId());
        List<ClassDashboardSummary> classSummaries = new ArrayList<>();

        BigDecimal branchFixed = ZERO, branchExtra = ZERO;
        BigDecimal branchCommitted = ZERO, branchPaid = ZERO, branchExtraDon = ZERO;

        for (EducationClass cls : classes) {
            int year = cls.getFinancialYear(referenceDate);
            if (year == 0) continue; // classe pas encore démarrée

            LocalDate periodStart = cls.getFinancialYearStart(year);

            BigDecimal fixed     = nvl(expenseRepository.getTotalExpensesByClassTypeAndYear(cls.getId(), ExpenseType.FIXED, year));
            BigDecimal extra     = nvl(expenseRepository.getTotalExpensesByClassTypeAndYear(cls.getId(), ExpenseType.EXTRA, year));
            BigDecimal committed = nvl(commitmentRepository.getTotalCommitmentsByClassAndYear(cls.getId(), year));
            BigDecimal paid      = nvl(paymentRepository.getTotalPaymentsByClassAndYear(cls.getId(), year));
            BigDecimal extraDon  = nvl(extraDonationRepository.getTotalByClassAndYear(cls.getId(), year));

            BigDecimal totalExp  = fixed.add(extra);
            BigDecimal remaining = committed.subtract(paid).max(ZERO);
            BigDecimal received  = paid.add(extraDon);
            BigDecimal balance   = received.subtract(totalExp);

            classSummaries.add(ClassDashboardSummary.builder()
                    .classId(cls.getId())
                    .classNameAr(cls.getNameAr())
                    .classNameFr(cls.getNameFr())
                    .financialYear(year)
                    .periodStart(periodStart)
                    .fixedExpenses(fixed)
                    .extraExpenses(extra)
                    .totalExpenses(totalExp)
                    .committedDonations(committed)
                    .paidDonations(paid)
                    .remainingDonations(remaining)
                    .extraDonations(extraDon)
                    .totalReceived(received)
                    .balance(balance)
                    .build());

            branchFixed    = branchFixed.add(fixed);
            branchExtra    = branchExtra.add(extra);
            branchCommitted = branchCommitted.add(committed);
            branchPaid      = branchPaid.add(paid);
            branchExtraDon  = branchExtraDon.add(extraDon);
        }

        BigDecimal branchTotalExp     = branchFixed.add(branchExtra);
        BigDecimal branchTotalReceived = branchPaid.add(branchExtraDon);
        BigDecimal branchLate         = branchCommitted.subtract(branchPaid).max(ZERO);
        BigDecimal branchBalance      = branchTotalReceived.subtract(branchTotalExp);

        return DashboardBranchSummary.builder()
                .id(branch.getId())
                .type(branch.getType())
                .nameAr(branch.getNameAr())
                .annualFixedExpenses(branchFixed)
                .annualExtraExpenses(branchExtra)
                .totalExpenses(branchTotalExp)
                .totalCommittedDonations(branchCommitted)
                .annualDonationsPaid(branchPaid)
                .annualDonationsLate(branchLate)
                .totalExtraDonations(branchExtraDon)
                .totalReceived(branchTotalReceived)
                .branchBalance(branchBalance)
                .classes(classSummaries)
                .build();
    }

    private int countUniqueDonors() {
        try {
            return (int) commitmentRepository.findAll()
                    .stream()
                    .map(c -> c.getDonor().getId())
                    .distinct()
                    .count();
        } catch (Exception e) {
            return 0;
        }
    }

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : ZERO;
    }
}
