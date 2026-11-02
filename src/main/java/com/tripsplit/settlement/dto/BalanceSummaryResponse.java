package com.tripsplit.settlement.dto;

import java.math.BigDecimal;
import java.util.List;

public class BalanceSummaryResponse {
    private Long tripId;
    private BigDecimal totalTripExpenses;
    private List<UserBalanceDTO> userBalances;
    private List<DebtRelationDTO> simplifiedDebts;

    public BalanceSummaryResponse() {}

    public BalanceSummaryResponse(Long tripId, BigDecimal totalTripExpenses,
                                  List<UserBalanceDTO> userBalances,
                                  List<DebtRelationDTO> simplifiedDebts) {
        this.tripId = tripId;
        this.totalTripExpenses = totalTripExpenses;
        this.userBalances = userBalances;
        this.simplifiedDebts = simplifiedDebts;
    }

    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public BigDecimal getTotalTripExpenses() {
        return totalTripExpenses;
    }

    public void setTotalTripExpenses(BigDecimal totalTripExpenses) {
        this.totalTripExpenses = totalTripExpenses;
    }

    public List<UserBalanceDTO> getUserBalances() {
        return userBalances;
    }

    public void setUserBalances(List<UserBalanceDTO> userBalances) {
        this.userBalances = userBalances;
    }

    public List<DebtRelationDTO> getSimplifiedDebts() {
        return simplifiedDebts;
    }

    public void setSimplifiedDebts(List<DebtRelationDTO> simplifiedDebts) {
        this.simplifiedDebts = simplifiedDebts;
    }
}
