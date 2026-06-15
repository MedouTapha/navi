package com.navi.education.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "extra_donations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtraDonation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donor_id", nullable = false)
    private Donor donor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private EducationClass educationClass;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate donationDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer financialYear;

    @Column(nullable = false)
    private Integer donationMonth;

    @PrePersist
    @PreUpdate
    public void calculateYearAndMonth() {
        if (educationClass != null && donationDate != null) {
            this.financialYear = educationClass.getFinancialYear(donationDate);
            this.donationMonth = donationDate.getMonthValue();
        }
    }
}
