package com.navi.education.service;

import com.navi.education.dto.request.EducationClassRequest;
import com.navi.education.dto.response.EducationClassResponse;
import com.navi.education.exception.ResourceNotFoundException;
import com.navi.education.model.entity.Branch;
import com.navi.education.model.entity.EducationClass;
import com.navi.education.model.enums.ClassType;
import com.navi.education.repository.BranchRepository;
import com.navi.education.repository.EducationClassRepository;
import com.navi.education.repository.ExpenseRepository;
import com.navi.education.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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
