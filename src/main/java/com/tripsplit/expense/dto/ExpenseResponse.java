package com.tripsplit.expense.dto;

import com.tripsplit.expense.entity.Expense;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseResponse {
    private Long id;
    private Long tripId;
    private Long activityId;
    private String activityName;
    private String title;
    private BigDecimal amount;
    private Long payerId;
    private String payerName;
    private LocalDate expenseDate;
    private String category;
    private LocalDateTime createdAt;
    private List<ExpenseShareResponse> shares;

    public ExpenseResponse() {}

    public static ExpenseResponse fromEntity(Expense expense) {
        if (expense == null) return null;
        ExpenseResponse dto = new ExpenseResponse();
        dto.setId(expense.getId());
        dto.setTitle(expense.getTitle());
        dto.setAmount(expense.getAmount());
        dto.setExpenseDate(expense.getExpenseDate());
        dto.setCategory(expense.getCategory());
        dto.setCreatedAt(expense.getCreatedAt());

        if (expense.getTrip() != null) {
            dto.setTripId(expense.getTrip().getId());
        }
        if (expense.getActivity() != null) {
            dto.setActivityId(expense.getActivity().getId());
            dto.setActivityName(expense.getActivity().getName());
        }
        if (expense.getPayer() != null) {
            dto.setPayerId(expense.getPayer().getId());
            dto.setPayerName(expense.getPayer().getName());
        }
        if (expense.getShares() != null) {
            dto.setShares(expense.getShares().stream()
                    .map(ExpenseShareResponse::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getPayerId() {
        return payerId;
    }

    public void setPayerId(Long payerId) {
        this.payerId = payerId;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ExpenseShareResponse> getShares() {
        return shares;
    }

    public void setShares(List<ExpenseShareResponse> shares) {
        this.shares = shares;
    }
}

