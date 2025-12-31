package com.navi.education.dto.response;

import com.navi.education.model.enums.BranchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponse {
    private Long id;
    private BranchType type;
    private String nameFr;
    private String nameAr;
    private String description;
    private long totalClasses;
    private BigDecimal monthlyExpenses;
}
