package com.tripsplit.activity.dto;

import com.tripsplit.activity.entity.Activity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ActivityResponse {
    private Long id;
    private Long tripId;
    private String name;
    private String category;
    private BigDecimal estimatedCost;
    private LocalDateTime createdAt;

    public ActivityResponse() {}

    public static ActivityResponse fromEntity(Activity activity) {
        if (activity == null) return null;
        ActivityResponse dto = new ActivityResponse();
        dto.setId(activity.getId());
        dto.setName(activity.getName());
        dto.setCategory(activity.getCategory());
        dto.setEstimatedCost(activity.getEstimatedCost());
        dto.setCreatedAt(activity.getCreatedAt());
        if (activity.getTrip() != null) {
            dto.setTripId(activity.getTrip().getId());
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
