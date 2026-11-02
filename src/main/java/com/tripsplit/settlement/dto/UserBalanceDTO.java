package com.tripsplit.settlement.dto;

import java.math.BigDecimal;

public class UserBalanceDTO {
    private Long userId;
    private String userName;
    private BigDecimal totalPaid;      // Paid in expenses
    private BigDecimal totalOwed;      // Share across expenses
    private BigDecimal settledPaid;    // Paid to others via settlements
    private BigDecimal settledReceived;// Received from others via settlements
    private BigDecimal netBalance;     // (totalPaid - totalOwed) + (settledPaid - settledReceived)
    private String status;             // OWES, GETS_BACK, SETTLED

    public UserBalanceDTO() {}

    public UserBalanceDTO(Long userId, String userName, BigDecimal totalPaid, BigDecimal totalOwed,
                          BigDecimal settledPaid, BigDecimal settledReceived, BigDecimal netBalance) {
        this.userId = userId;
        this.userName = userName;
        this.totalPaid = totalPaid;
        this.totalOwed = totalOwed;
        this.settledPaid = settledPaid;
        this.settledReceived = settledReceived;
        this.netBalance = netBalance;

        if (netBalance.compareTo(BigDecimal.ZERO) > 0) {
            this.status = "GETS_BACK";
        } else if (netBalance.compareTo(BigDecimal.ZERO) < 0) {
            this.status = "OWES";
        } else {
            this.status = "SETTLED";
        }
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public BigDecimal getTotalOwed() {
        return totalOwed;
    }

    public void setTotalOwed(BigDecimal totalOwed) {
        this.totalOwed = totalOwed;
    }

    public BigDecimal getSettledPaid() {
        return settledPaid;
    }

    public void setSettledPaid(BigDecimal settledPaid) {
        this.settledPaid = settledPaid;
    }

    public BigDecimal getSettledReceived() {
        return settledReceived;
    }

    public void setSettledReceived(BigDecimal settledReceived) {
        this.settledReceived = settledReceived;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

