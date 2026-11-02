package com.tripsplit.expense.dto;

import com.tripsplit.expense.entity.ExpenseShare;
import java.math.BigDecimal;

public class ExpenseShareResponse {
    private Long id;
    private Long userId;
    private String userName;
    private BigDecimal shareAmount;

    public ExpenseShareResponse() {}

    public static ExpenseShareResponse fromEntity(ExpenseShare share) {
        if (share == null) return null;
        ExpenseShareResponse dto = new ExpenseShareResponse();
        dto.setId(share.getId());
        dto.setShareAmount(share.getShareAmount());
        if (share.getUser() != null) {
            dto.setUserId(share.getUser().getId());
            dto.setUserName(share.getUser().getName());
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public BigDecimal getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(BigDecimal shareAmount) {
        this.shareAmount = shareAmount;
    }
}
