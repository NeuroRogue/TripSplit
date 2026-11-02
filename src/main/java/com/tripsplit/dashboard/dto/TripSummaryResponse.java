package com.tripsplit.dashboard.dto;

import com.tripsplit.settlement.dto.DebtRelationDTO;
import com.tripsplit.settlement.dto.UserBalanceDTO;
import java.math.BigDecimal;
import java.util.List;

public class TripSummaryResponse {
    private Long tripId;
    private String title;
    private String destination;
    private BigDecimal totalExpense;
    private int totalActivities;
    private int totalExpensesCount;
    private int totalMembers;
    private List<CategorySpendDTO> categoryBreakdown;
    private List<UserBalanceDTO> balances;
    private List<DebtRelationDTO> simplifiedDebts;

    public TripSummaryResponse() {}

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public int getTotalActivities() {
        return totalActivities;
    }

    public void setTotalActivities(int totalActivities) {
        this.totalActivities = totalActivities;
    }

    public int getTotalExpensesCount() {
        return totalExpensesCount;
    }

    public void setTotalExpensesCount(int totalExpensesCount) {
        this.totalExpensesCount = totalExpensesCount;
    }

    public int getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(int totalMembers) {
        this.totalMembers = totalMembers;
    }

    public List<CategorySpendDTO> getCategoryBreakdown() {
        return categoryBreakdown;
    }

    public void setCategoryBreakdown(List<CategorySpendDTO> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }

    public List<UserBalanceDTO> getBalances() {
        return balances;
    }

    public void setBalances(List<UserBalanceDTO> balances) {
        this.balances = balances;
    }

    public List<DebtRelationDTO> getSimplifiedDebts() {
        return simplifiedDebts;
    }

    public void setSimplifiedDebts(List<DebtRelationDTO> simplifiedDebts) {
        this.simplifiedDebts = simplifiedDebts;
    }
}

