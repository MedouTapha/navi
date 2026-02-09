package com.navi.education.repository;

import com.navi.education.model.entity.Payment;
import com.navi.education.model.enums.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByCommitmentId(Long commitmentId);

    List<Payment> findByCommitmentDonorId(Long donorId);

    List<Payment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    List<Payment> findByPaymentMethod(PaymentMethod paymentMethod);

    @Query("SELECT p FROM Payment p WHERE p.commitment.donor.id = :donorId " +
           "ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentsByDonor(@Param("donorId") Long donorId);

    @Query("SELECT p FROM Payment p WHERE p.commitment.educationClass.id = :classId " +
           "ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentsByClass(@Param("classId") Long classId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.commitment.id = :commitmentId")
    BigDecimal getTotalPaymentsByCommitment(@Param("commitmentId") Long commitmentId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.commitment.donor.id = :donorId")
    BigDecimal getTotalPaymentsByDonor(@Param("donorId") Long donorId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.commitment.educationClass.id = :classId " +
           "AND p.commitment.financialYear = :financialYear")
    BigDecimal getTotalPaymentsByClassAndYear(@Param("classId") Long classId,
                                               @Param("financialYear") Integer financialYear);

    @Query("SELECT SUM(p.amount) FROM Payment p " +
           "WHERE p.commitment.educationClass.branch.id = :branchId")
    BigDecimal getTotalPaymentsByBranch(@Param("branchId") Long branchId);
}
