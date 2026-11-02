package com.tripsplit.activity.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class CreateActivityRequest {

    @NotBlank(message = "Activity name is required")
    private String name;

    @NotBlank(message = "Category is required (e.g. HOTEL, FOOD, SIGHTSEEING, TRAVEL, SHOPPING, OTHER)")
    private String category;

    @DecimalMin(value = "0.00", message = "Estimated cost must be 0 or positive")
    private BigDecimal estimatedCost = BigDecimal.ZERO;

    public CreateActivityRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(BigDecimal estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
}

