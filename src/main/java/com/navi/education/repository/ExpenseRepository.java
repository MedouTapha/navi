package com.navi.education.repository;

import com.navi.education.model.entity.Expense;
import com.navi.education.model.enums.ExpenseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByEducationClassId(Long classId);

    List<Expense> findByEducationClassIdAndExpenseType(Long classId, ExpenseType expenseType);

    List<Expense> findByEducationClassIdAndFinancialYear(Long classId, Integer financialYear);

    List<Expense> findByEducationClassIdAndExpenseDateBetween(Long classId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT e FROM Expense e WHERE e.educationClass.id = :classId " +
           "AND e.expenseDate BETWEEN :startDate AND :endDate " +
           "ORDER BY e.expenseDate DESC")
    List<Expense> findExpensesByClassAndPeriod(@Param("classId") Long classId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.educationClass.id = :classId")
    BigDecimal getTotalExpensesByClass(@Param("classId") Long classId);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.educationClass.id = :classId " +
           "AND e.financialYear = :financialYear")
    BigDecimal getTotalExpensesByClassAndYear(@Param("classId") Long classId,
                                               @Param("financialYear") Integer financialYear);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.educationClass.id = :classId " +
           "AND e.expenseType = :type AND e.financialYear = :year")
    BigDecimal getTotalExpensesByClassTypeAndYear(@Param("classId") Long classId,
                                                   @Param("type") ExpenseType type,
                                                   @Param("year") Integer year);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.educationClass.branch.id = :branchId " +
           "AND e.expenseDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpensesByBranchAndPeriod(@Param("branchId") Long branchId,
                                                  @Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.educationClass.branch.id = :branchId " +
           "AND e.expenseType = :expenseType")
    BigDecimal getTotalExpensesByBranchAndType(@Param("branchId") Long branchId,
                                                @Param("expenseType") ExpenseType expenseType);

    @Query("SELECT SUM(e.amount) FROM Expense e WHERE e.educationClass.branch.id = :branchId " +
           "AND e.expenseType = :expenseType AND e.financialYear = :financialYear")
    BigDecimal getTotalExpensesByBranchTypeAndYear(@Param("branchId") Long branchId,
                                                     @Param("expenseType") ExpenseType expenseType,
                                                     @Param("financialYear") Integer financialYear);

    boolean existsByEducationClassIdAndExpenseTypeAndExpenseDateBetween(
            Long classId, ExpenseType expenseType, LocalDate startDate, LocalDate endDate);

    @Query("SELECT e FROM Expense e " +
           "JOIN FETCH e.educationClass c " +
           "JOIN FETCH c.branch b " +
           "WHERE b.id = :branchId " +
           "AND e.expenseDate BETWEEN :startDate AND :endDate " +
           "ORDER BY e.expenseType, c.id, e.id")
    List<Expense> findByBranchAndPeriod(@Param("branchId") Long branchId,
                                         @Param("startDate") LocalDate startDate,
                                         @Param("endDate") LocalDate endDate);
}
