package com.tripsplit.settlement.dto;

import com.tripsplit.settlement.entity.Settlement;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class SettlementResponse {
    private Long id;
    private Long tripId;
    private Long payerId;
    private String payerName;
    private Long payeeId;
    private String payeeName;
    private BigDecimal amount;
    private LocalDate settlementDate;
    private String notes;
    private LocalDateTime createdAt;

    public SettlementResponse() {}

    public static SettlementResponse fromEntity(Settlement s) {
        if (s == null) return null;
        SettlementResponse dto = new SettlementResponse();
        dto.setId(s.getId());
        dto.setAmount(s.getAmount());
        dto.setSettlementDate(s.getSettlementDate());
        dto.setNotes(s.getNotes());
        dto.setCreatedAt(s.getCreatedAt());
        if (s.getTrip() != null) {
            dto.setTripId(s.getTrip().getId());
        }
        if (s.getPayer() != null) {
            dto.setPayerId(s.getPayer().getId());
            dto.setPayerName(s.getPayer().getName());
        }
        if (s.getPayee() != null) {
            dto.setPayeeId(s.getPayee().getId());
            dto.setPayeeName(s.getPayee().getName());
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

    public Long getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Long payeeId) {
        this.payeeId = payeeId;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getSettlementDate() {
        return settlementDate;
    }

    public void setSettlementDate(LocalDate settlementDate) {
        this.settlementDate = settlementDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
