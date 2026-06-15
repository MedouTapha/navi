package com.navi.education.service;

import com.navi.education.dto.request.EducationClassRequest;
import com.navi.education.dto.response.ClassFinancialYearSummary;
import com.navi.education.dto.response.EducationClassResponse;
import com.navi.education.exception.ResourceNotFoundException;
import com.navi.education.model.entity.Branch;
import com.navi.education.model.entity.EducationClass;
import com.navi.education.model.enums.ClassType;
import com.navi.education.model.enums.ExpenseType;
import com.navi.education.repository.AnnualCommitmentRepository;
import com.navi.education.repository.BranchRepository;
import com.navi.education.repository.EducationClassRepository;
import com.navi.education.repository.ExpenseRepository;
import com.navi.education.repository.ExtraDonationRepository;
import com.navi.education.repository.PaymentRepository;
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
@Transactional
public class EducationClassService {

    private final EducationClassRepository classRepository;
    private final BranchRepository branchRepository;
    private final ExpenseRepository expenseRepository;
    private final PaymentRepository paymentRepository;
    private final AnnualCommitmentRepository commitmentRepository;
    private final ExtraDonationRepository extraDonationRepository;

    public EducationClassResponse createClass(EducationClassRequest request) {
        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branche", request.getBranchId()));

        EducationClass educationClass = EducationClass.builder()
                .nameFr(request.getNameFr())
                .nameAr(request.getNameAr())
                .classType(request.getClassType())
                .branch(branch)
                .monthlyFixedAmount(request.getMonthlyFixedAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .active(request.getActive() != null ? request.getActive() : true)
                .description(request.getDescription())
                .build();

        educationClass = classRepository.save(educationClass);
        log.info("Classe créée: {} - {}", educationClass.getNameFr(), educationClass.getId());

        return toClassResponse(educationClass);
    }

    public EducationClassResponse updateClass(Long id, EducationClassRequest request) {
        EducationClass educationClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classe", id));

        if (request.getBranchId() != null && !request.getBranchId().equals(educationClass.getBranch().getId())) {
            Branch branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Branche", request.getBranchId()));
            educationClass.setBranch(branch);
        }

        if (request.getNameFr() != null) educationClass.setNameFr(request.getNameFr());
        if (request.getNameAr() != null) educationClass.setNameAr(request.getNameAr());
        if (request.getClassType() != null) educationClass.setClassType(request.getClassType());
        if (request.getMonthlyFixedAmount() != null) educationClass.setMonthlyFixedAmount(request.getMonthlyFixedAmount());
        if (request.getStartDate() != null) educationClass.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) educationClass.setEndDate(request.getEndDate());
        if (request.getActive() != null) educationClass.setActive(request.getActive());
        if (request.getDescription() != null) educationClass.setDescription(request.getDescription());

        educationClass = classRepository.save(educationClass);
        log.info("Classe mise à jour: {}", educationClass.getId());

        return toClassResponse(educationClass);
    }

    @Transactional(readOnly = true)
    public List<EducationClassResponse> getAllClasses() {
        return classRepository.findAll().stream()
                .map(this::toClassResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EducationClassResponse> getClassesByBranch(Long branchId) {
        return classRepository.findByBranchId(branchId).stream()
                .map(this::toClassResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EducationClassResponse> getClassesByType(ClassType classType) {
        return classRepository.findByClassType(classType).stream()
                .map(this::toClassResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EducationClassResponse> getClassesByBranchAndType(Long branchId, ClassType classType) {
        return classRepository.findByBranchIdAndClassType(branchId, classType).stream()
                .map(this::toClassResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EducationClassResponse getClassById(Long id) {
        EducationClass educationClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classe", id));
        return toClassResponse(educationClass);
    }

    /**
     * Historique du bilan de la classe par année financière, de l'année de
     * démarrage jusqu'à l'année financière en cours (incluse).
     */
    @Transactional(readOnly = true)
    public List<ClassFinancialYearSummary> getFinancialYearsHistory(Long id) {
        EducationClass educationClass = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classe", id));

        int currentYear = educationClass.getFinancialYear(LocalDate.now());
        if (currentYear == 0) {
            return List.of();
        }

        List<ClassFinancialYearSummary> history = new ArrayList<>();
        for (int year = educationClass.getStartDate().getYear(); year <= currentYear; year++) {
            BigDecimal fixed = nvl(expenseRepository.getTotalExpensesByClassTypeAndYear(id, ExpenseType.FIXED, year));
            BigDecimal extra = nvl(expenseRepository.getTotalExpensesByClassTypeAndYear(id, ExpenseType.EXTRA, year));
            BigDecimal committed = nvl(commitmentRepository.getTotalCommitmentsByClassAndYear(id, year));
            BigDecimal paid = nvl(paymentRepository.getTotalPaymentsByClassAndYear(id, year));
            BigDecimal extraDon = nvl(extraDonationRepository.getTotalByClassAndYear(id, year));

            BigDecimal totalExpenses = fixed.add(extra);
            BigDecimal remaining = committed.subtract(paid).max(BigDecimal.ZERO);
            BigDecimal totalReceived = paid.add(extraDon);
            BigDecimal balance = totalReceived.subtract(totalExpenses);

            history.add(ClassFinancialYearSummary.builder()
                    .financialYear(year)
                    .periodStart(educationClass.getFinancialYearStart(year))
                    .periodEnd(educationClass.getFinancialYearEnd(year))
                    .current(year == currentYear)
                    .fixedExpenses(fixed)
                    .extraExpenses(extra)
                    .totalExpenses(totalExpenses)
                    .committedDonations(committed)
                    .paidDonations(paid)
                    .remainingDonations(remaining)
                    .extraDonations(extraDon)
                    .totalReceived(totalReceived)
                    .balance(balance)
                    .build());
        }

        history.sort((a, b) -> b.getFinancialYear() - a.getFinancialYear());
        return history;
    }

    private BigDecimal nvl(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    public void deleteClass(Long id) {
        if (!classRepository.existsById(id)) {
            throw new ResourceNotFoundException("Classe", id);
        }
        classRepository.deleteById(id);
        log.info("Classe supprimée: {}", id);
    }

    private EducationClassResponse toClassResponse(EducationClass educationClass) {
        BigDecimal totalExpenses = expenseRepository.getTotalExpensesByClass(educationClass.getId());
        Integer currentYear = educationClass.getFinancialYear(LocalDate.now());
        BigDecimal totalDonations = paymentRepository.getTotalPaymentsByClassAndYear(
                educationClass.getId(), currentYear);

        return EducationClassResponse.builder()
                .id(educationClass.getId())
                .nameFr(educationClass.getNameFr())
                .nameAr(educationClass.getNameAr())
                .classType(educationClass.getClassType())
                .branchId(educationClass.getBranch().getId())
                .branchName(educationClass.getBranch().getNameFr())
                .monthlyFixedAmount(educationClass.getMonthlyFixedAmount())
                .startDate(educationClass.getStartDate())
                .endDate(educationClass.getEndDate())
                .active(educationClass.getActive())
                .description(educationClass.getDescription())
                .totalExpenses(totalExpenses != null ? totalExpenses : BigDecimal.ZERO)
                .totalDonations(totalDonations != null ? totalDonations : BigDecimal.ZERO)
                .currentFinancialYear(currentYear)
                .build();
    }
}
