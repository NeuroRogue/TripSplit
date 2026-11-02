package com.tripsplit.dashboard.service;

import com.tripsplit.activity.repository.ActivityRepository;
import com.tripsplit.common.MoneyUtils;
import com.tripsplit.dashboard.dto.CategorySpendDTO;
import com.tripsplit.dashboard.dto.DashboardSummaryResponse;
import com.tripsplit.dashboard.dto.TripSummaryResponse;
import com.tripsplit.expense.dto.ExpenseResponse;
import com.tripsplit.expense.entity.Expense;
import com.tripsplit.expense.repository.ExpenseRepository;
import com.tripsplit.settlement.dto.BalanceSummaryResponse;
import com.tripsplit.settlement.service.SettlementService;
import com.tripsplit.trip.dto.TripResponse;
import com.tripsplit.trip.entity.Trip;
import com.tripsplit.trip.service.TripService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final TripService tripService;
    private final ExpenseRepository expenseRepository;
    private final ActivityRepository activityRepository;
    private final SettlementService settlementService;

    public DashboardService(TripService tripService,
                            ExpenseRepository expenseRepository,
                            ActivityRepository activityRepository,
                            SettlementService settlementService) {
        this.tripService = tripService;
        this.expenseRepository = expenseRepository;
        this.activityRepository = activityRepository;
        this.settlementService = settlementService;
    }

    public DashboardSummaryResponse getOverallSummary() {
        List<TripResponse> trips = tripService.getAllTrips();
        List<Expense> allExpenses = expenseRepository.findAll();

        BigDecimal totalSpend = allExpenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<ExpenseResponse> recent = expenseRepository.findTop5ByOrderByCreatedAtDesc().stream()
                .map(ExpenseResponse::fromEntity)
                .collect(Collectors.toList());

        // Category breakdown across all expenses
        Map<String, BigDecimal> categoryTotals = new HashMap<>();
        for (Expense exp : allExpenses) {
            String cat = exp.getCategory() != null ? exp.getCategory() : "OTHER";
            categoryTotals.put(cat, categoryTotals.getOrDefault(cat, BigDecimal.ZERO).add(exp.getAmount()));
        }

        List<CategorySpendDTO> topCategories = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : categoryTotals.entrySet()) {
            double pct = totalSpend.compareTo(BigDecimal.ZERO) > 0
                    ? entry.getValue().divide(totalSpend, 4, RoundingMode.HALF_UP).doubleValue() * 100
                    : 0.0;
            topCategories.add(new CategorySpendDTO(entry.getKey(), MoneyUtils.round(entry.getValue()), Math.round(pct * 10.0) / 10.0));
        }
        topCategories.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));

        DashboardSummaryResponse res = new DashboardSummaryResponse();
        res.setTotalTrips(trips.size());
        res.setTotalGroupSpend(MoneyUtils.round(totalSpend));
        res.setTotalExpensesCount(allExpenses.size());
        res.setTrips(trips);
        res.setRecentExpenses(recent);
        res.setTopCategories(topCategories);
        return res;
    }

    public TripSummaryResponse getTripSummary(Long tripId) {
        Trip trip = tripService.getTripEntity(tripId);
        List<Expense> expenses = expenseRepository.findByTripIdOrderByExpenseDateDescCreatedAtDesc(tripId);
        int activityCount = activityRepository.findByTripIdOrderByCreatedAtDesc(tripId).size();
        BalanceSummaryResponse balances = settlementService.calculateBalances(tripId);

        BigDecimal totalSpend = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> categoryMap = new HashMap<>();
        for (Expense exp : expenses) {
            String cat = exp.getCategory() != null ? exp.getCategory() : "OTHER";
            categoryMap.put(cat, categoryMap.getOrDefault(cat, BigDecimal.ZERO).add(exp.getAmount()));
        }

        List<CategorySpendDTO> breakdown = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : categoryMap.entrySet()) {
            double pct = totalSpend.compareTo(BigDecimal.ZERO) > 0
                    ? entry.getValue().divide(totalSpend, 4, RoundingMode.HALF_UP).doubleValue() * 100
                    : 0.0;
            breakdown.add(new CategorySpendDTO(entry.getKey(), MoneyUtils.round(entry.getValue()), Math.round(pct * 10.0) / 10.0));
        }
        breakdown.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));

        TripSummaryResponse res = new TripSummaryResponse();
        res.setTripId(trip.getId());
        res.setTitle(trip.getTitle());
        res.setDestination(trip.getDestination());
        res.setTotalExpense(MoneyUtils.round(totalSpend));
        res.setTotalActivities(activityCount);
        res.setTotalExpensesCount(expenses.size());
        res.setTotalMembers(balances.getUserBalances().size());
        res.setCategoryBreakdown(breakdown);
        res.setBalances(balances.getUserBalances());
        res.setSimplifiedDebts(balances.getSimplifiedDebts());
        return res;
    }
}

