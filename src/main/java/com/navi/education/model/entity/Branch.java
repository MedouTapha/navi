package com.navi.education.model.entity;

import com.navi.education.model.enums.BranchType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "branches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private BranchType type;

    @Column(nullable = false)
    private String nameFr;

    @Column(nullable = false)
    private String nameAr;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<EducationClass> classes = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    @PrePersist
    @PreUpdate
    public void updateNames() {
        if (type != null) {
            this.nameFr = type.getFrenchName();
            this.nameAr = type.getArabicName();
        }
    }
}
