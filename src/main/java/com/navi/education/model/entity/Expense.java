package com.navi.education.model.entity;

import com.navi.education.model.enums.ExpenseType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private EducationClass educationClass;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseType expenseType;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate expenseDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer financialYear;

    @Column(nullable = false)
    private Integer month; // 1-12

    @PrePersist
    @PreUpdate
    public void calculateFinancialYearAndMonth() {
        if (educationClass != null && expenseDate != null) {
            this.financialYear = educationClass.getFinancialYear(expenseDate);
            this.month = expenseDate.getMonthValue();
        }
    }
}
