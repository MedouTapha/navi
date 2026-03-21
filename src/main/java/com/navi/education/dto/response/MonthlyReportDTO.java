package com.navi.education.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class MonthlyReportDTO {
    private int month;
    private int year;
    private String monthNameAr;
    private List<MonthlyReportBranchDTO> branches;
    private BigDecimal grandTotal;
}
