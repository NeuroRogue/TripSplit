package com.tripsplit.trip.dto;

import com.tripsplit.trip.entity.Trip;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TripResponse {
    private Long id;
    private String title;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long createdById;
    private String createdByName;
    private int memberCount;
    private LocalDateTime createdAt;

    public TripResponse() {}

    public static TripResponse fromEntity(Trip trip, int memberCount) {
        if (trip == null) return null;
        TripResponse dto = new TripResponse();
        dto.setId(trip.getId());
        dto.setTitle(trip.getTitle());
        dto.setDestination(trip.getDestination());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());
        dto.setMemberCount(memberCount);
        dto.setCreatedAt(trip.getCreatedAt());
        if (trip.getCreatedBy() != null) {
            dto.setCreatedById(trip.getCreatedBy().getId());
            dto.setCreatedByName(trip.getCreatedBy().getName());
        }
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
