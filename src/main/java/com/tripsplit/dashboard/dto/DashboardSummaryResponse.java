package com.tripsplit.dashboard.dto;

import com.tripsplit.expense.dto.ExpenseResponse;
import com.tripsplit.trip.dto.TripResponse;
import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryResponse {
    private int totalTrips;
    private BigDecimal totalGroupSpend;
    private int totalExpensesCount;
    private List<TripResponse> trips;
    private List<ExpenseResponse> recentExpenses;
    private List<CategorySpendDTO> topCategories;

    public DashboardSummaryResponse() {}

    public int getTotalTrips() {
        return totalTrips;
    }

    public void setTotalTrips(int totalTrips) {
        this.totalTrips = totalTrips;
    }

    public BigDecimal getTotalGroupSpend() {
        return totalGroupSpend;
    }

    public void setTotalGroupSpend(BigDecimal totalGroupSpend) {
        this.totalGroupSpend = totalGroupSpend;
    }

    public int getTotalExpensesCount() {
        return totalExpensesCount;
    }

    public void setTotalExpensesCount(int totalExpensesCount) {
        this.totalExpensesCount = totalExpensesCount;
    }

    public List<TripResponse> getTrips() {
        return trips;
    }

    public void setTrips(List<TripResponse> trips) {
        this.trips = trips;
    }

    public List<ExpenseResponse> getRecentExpenses() {
        return recentExpenses;
    }

    public void setRecentExpenses(List<ExpenseResponse> recentExpenses) {
        this.recentExpenses = recentExpenses;
    }

    public List<CategorySpendDTO> getTopCategories() {
        return topCategories;
    }

    public void setTopCategories(List<CategorySpendDTO> topCategories) {
        this.topCategories = topCategories;
    }
}
