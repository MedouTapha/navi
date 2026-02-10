package com.navi.education.model.entity;

import com.navi.education.model.enums.ClassType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "education_classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nameFr;

    @Column(nullable = false)
    private String nameAr;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassType classType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyFixedAmount;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column
    private LocalDate endDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "educationClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Expense> expenses = new ArrayList<>();

    @OneToMany(mappedBy = "educationClass", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AnnualCommitment> commitments = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Calcule l'année financière basée sur la date de début et une date donnée
     * Retourne l'année calendaire réelle (ex: 2024, 2025, 2026)
     */
    public int getFinancialYear(LocalDate date) {
        if (date.isBefore(startDate)) {
            return 0;
        }

        LocalDate currentYearStart = startDate;
        int yearCount = 1;

        while (currentYearStart.plusYears(1).isBefore(date) || currentYearStart.plusYears(1).isEqual(date)) {
            currentYearStart = currentYearStart.plusYears(1);
            yearCount++;
        }

        // Retourne l'année calendaire réelle au lieu d'un compteur
        return startDate.getYear() + (yearCount - 1);
    }

    /**
     * Retourne le début de l'année financière pour une année donnée
     */
    public LocalDate getFinancialYearStart(int year) {
        return startDate.plusYears(year - 1);
    }

    /**
     * Retourne la fin de l'année financière pour une année donnée
     */
    public LocalDate getFinancialYearEnd(int year) {
        return startDate.plusYears(year).minusDays(1);
    }
}
