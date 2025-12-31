package com.navi.education.repository;

import com.navi.education.model.entity.Branch;
import com.navi.education.model.enums.BranchType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByType(BranchType type);

    @Query("SELECT COUNT(c) FROM EducationClass c WHERE c.branch.id = :branchId AND c.active = true")
    long countActiveClassesByBranchId(Long branchId);
}
