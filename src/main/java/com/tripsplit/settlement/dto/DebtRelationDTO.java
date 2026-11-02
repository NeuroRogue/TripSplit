package com.tripsplit.settlement.dto;

import java.math.BigDecimal;

public class DebtRelationDTO {
    private Long debtorId;
    private String debtorName;
    private Long creditorId;
    private String creditorName;
    private BigDecimal amount;

    public DebtRelationDTO() {}

    public DebtRelationDTO(Long debtorId, String debtorName, Long creditorId, String creditorName, BigDecimal amount) {
        this.debtorId = debtorId;
        this.debtorName = debtorName;
        this.creditorId = creditorId;
        this.creditorName = creditorName;
        this.amount = amount;
    }

    public Long getDebtorId() {
        return debtorId;
    }

    public void setDebtorId(Long debtorId) {
        this.debtorId = debtorId;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }

    public Long getCreditorId() {
        return creditorId;
    }

    public void setCreditorId(Long creditorId) {
        this.creditorId = creditorId;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

