package com.navi.education.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class MonthlyReportBranchDTO {
    private String branchNameAr;
    private List<MonthlyReportLineDTO> lines;
    private BigDecimal total;
}
