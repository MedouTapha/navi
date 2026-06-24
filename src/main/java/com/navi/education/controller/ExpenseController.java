package com.navi.education.controller;

import com.navi.education.dto.request.ExpenseRequest;
import com.navi.education.dto.response.ExpenseResponse;
import com.navi.education.model.enums.ExpenseType;
import com.navi.education.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping("/extra")
    public ResponseEntity<ExpenseResponse> createExtraExpense(@Valid @RequestBody ExpenseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseService.createExtraExpense(request));
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) ExpenseType type,
            @RequestParam(required = false) Integer financialYear,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        if (classId != null && startDate != null && endDate != null) {
            return ResponseEntity.ok(expenseService.getExpensesByClassAndPeriod(classId, startDate, endDate));
        } else if (classId != null && financialYear != null) {
            return ResponseEntity.ok(expenseService.getExpensesByClassAndYear(classId, financialYear));
        } else if (classId != null && type != null) {
            return ResponseEntity.ok(expenseService.getExpensesByClassAndType(classId, type));
        } else if (classId != null) {
            return ResponseEntity.ok(expenseService.getExpensesByClass(classId));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable Long id) {
        return ResponseEntity.ok(expenseService.getExpenseById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
