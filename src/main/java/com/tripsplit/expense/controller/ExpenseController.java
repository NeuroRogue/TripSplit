package com.tripsplit.expense.controller;

import com.tripsplit.common.ApiResponse;
import com.tripsplit.expense.dto.CreateExpenseRequest;
import com.tripsplit.expense.dto.ExpenseResponse;
import com.tripsplit.expense.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/trips/{tripId}/expenses")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getTripExpenses(@PathVariable Long tripId) {
        List<ExpenseResponse> expenses = expenseService.getExpensesByTrip(tripId);
        return ResponseEntity.ok(ApiResponse.ok("Expenses retrieved successfully", expenses));
    }

    @PostMapping("/trips/{tripId}/expenses")
    public ResponseEntity<ApiResponse<ExpenseResponse>> recordExpense(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateExpenseRequest request) {
        ExpenseResponse expense = expenseService.recordExpense(tripId, request);
        return ResponseEntity.ok(ApiResponse.ok("Expense recorded and split successfully", expense));
    }

    @GetMapping("/expenses/{expenseId}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> getExpense(@PathVariable Long expenseId) {
        ExpenseResponse expense = expenseService.getExpenseById(expenseId);
        return ResponseEntity.ok(ApiResponse.ok("Expense details retrieved successfully", expense));
    }
}
