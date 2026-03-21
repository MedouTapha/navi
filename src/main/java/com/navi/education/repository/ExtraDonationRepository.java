package com.navi.education.repository;

import com.navi.education.model.entity.ExtraDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ExtraDonationRepository extends JpaRepository<ExtraDonation, Long> {

    List<ExtraDonation> findByEducationClassId(Long classId);

    List<ExtraDonation> findByDonorId(Long donorId);

    @Query("SELECT SUM(d.amount) FROM ExtraDonation d WHERE d.educationClass.id = :classId " +
           "AND d.financialYear = :year")
    BigDecimal getTotalByClassAndYear(@Param("classId") Long classId,
                                      @Param("year") Integer year);

    @Query("SELECT SUM(d.amount) FROM ExtraDonation d WHERE d.educationClass.branch.id = :branchId " +
           "AND d.financialYear = :year")
    BigDecimal getTotalByBranchAndYear(@Param("branchId") Long branchId,
                                        @Param("year") Integer year);
}
