package com.navi.education.repository;

import com.navi.education.model.entity.EducationClass;
import com.navi.education.model.enums.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationClassRepository extends JpaRepository<EducationClass, Long> {

    List<EducationClass> findByBranchId(Long branchId);

    List<EducationClass> findByClassType(ClassType classType);

    List<EducationClass> findByBranchIdAndClassType(Long branchId, ClassType classType);

    List<EducationClass> findByActiveTrue();

    @Query("SELECT c FROM EducationClass c WHERE c.active = true AND c.branch.id = :branchId")
    List<EducationClass> findActiveClassesByBranch(@Param("branchId") Long branchId);
}
