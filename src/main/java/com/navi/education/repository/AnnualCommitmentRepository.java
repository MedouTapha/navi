package com.navi.education.repository;

import com.navi.education.model.entity.AnnualCommitment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnnualCommitmentRepository extends JpaRepository<AnnualCommitment, Long> {

    List<AnnualCommitment> findByDonorId(Long donorId);

    List<AnnualCommitment> findByEducationClassId(Long classId);

    List<AnnualCommitment> findByEducationClassIdAndFinancialYear(Long classId, Integer financialYear);

    Optional<AnnualCommitment> findByDonorIdAndEducationClassIdAndFinancialYear(
            Long donorId, Long classId, Integer financialYear);

    boolean existsByDonorIdAndEducationClassIdAndFinancialYear(
            Long donorId, Long classId, Integer financialYear);

    @Query("SELECT ac FROM AnnualCommitment ac WHERE ac.educationClass.id = :classId " +
           "AND ac.financialYear = :financialYear AND ac.active = true")
    List<AnnualCommitment> findActiveCommitmentsByClassAndYear(@Param("classId") Long classId,
                                                                 @Param("financialYear") Integer financialYear);

    @Query("SELECT SUM(ac.annualAmount) FROM AnnualCommitment ac " +
           "WHERE ac.educationClass.id = :classId AND ac.financialYear = :financialYear")
    BigDecimal getTotalCommitmentsByClassAndYear(@Param("classId") Long classId,
                                                  @Param("financialYear") Integer financialYear);

    @Query("SELECT ac FROM AnnualCommitment ac LEFT JOIN FETCH ac.payments " +
           "WHERE ac.id = :commitmentId")
    Optional<AnnualCommitment> findByIdWithPayments(@Param("commitmentId") Long commitmentId);

    @Query("SELECT SUM(ac.annualAmount) FROM AnnualCommitment ac " +
           "WHERE ac.educationClass.branch.id = :branchId")
    BigDecimal getTotalCommitmentsByBranch(@Param("branchId") Long branchId);

    @Query("SELECT SUM(ac.annualAmount) FROM AnnualCommitment ac " +
           "WHERE ac.educationClass.branch.id = :branchId AND ac.financialYear = :financialYear")
    BigDecimal getTotalCommitmentsByBranchAndYear(@Param("branchId") Long branchId,
                                                    @Param("financialYear") Integer financialYear);

    @Query("SELECT COUNT(DISTINCT ac.donor.id) FROM AnnualCommitment ac " +
           "WHERE ac.educationClass.branch.id = :branchId")
    Integer countUniqueDonorsByBranch(@Param("branchId") Long branchId);
}
