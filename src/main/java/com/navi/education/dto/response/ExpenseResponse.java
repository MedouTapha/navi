package com.navi.education.dto.response;

import com.navi.education.model.enums.ExpenseType;
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
public class ExpenseResponse {
    private Long id;
    private Long classId;
    private String className;
    private ExpenseType expenseType;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private String description;
    private Integer financialYear;
    private Integer expenseMonth;
}
