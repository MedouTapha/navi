package com.navi.education.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "annual_commitments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"donor_id", "class_id", "financial_year"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnualCommitment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private EducationClass educationClass;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal annualAmount;

    @Column(nullable = false)
    private LocalDate commitmentDate;

    @Column(nullable = false)
    private Integer financialYear;

    @OneToMany(mappedBy = "commitment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @PrePersist
    @PreUpdate
    public void calculateFinancialYear() {
        if (educationClass != null && commitmentDate != null) {
            this.financialYear = educationClass.getFinancialYear(commitmentDate);
        }
    }

    /**
     * Calcule le montant total payé
     */
    @Transient
    public BigDecimal getTotalPaid() {
        return payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcule le solde restant à payer
     */
    @Transient
    public BigDecimal getRemainingBalance() {
        return annualAmount.subtract(getTotalPaid());
    }

    /**
     * Vérifie si l'engagement est complètement payé
     */
    @Transient
    public boolean isFullyPaid() {
        return getRemainingBalance().compareTo(BigDecimal.ZERO) <= 0;
    }
}
