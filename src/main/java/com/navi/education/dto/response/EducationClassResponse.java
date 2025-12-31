package com.navi.education.dto.response;

import com.navi.education.model.enums.ClassType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationClassResponse {
    private Long id;
    private String nameFr;
    private String nameAr;
    private ClassType classType;
    private Long branchId;
    private String branchName;
    private BigDecimal monthlyFixedAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean active;
    private String description;
    private BigDecimal totalExpenses;
    private BigDecimal totalDonations;
    private Integer currentFinancialYear;
}
