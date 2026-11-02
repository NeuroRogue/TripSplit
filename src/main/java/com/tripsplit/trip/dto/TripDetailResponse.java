package com.tripsplit.trip.dto;

import com.tripsplit.trip.entity.Trip;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TripDetailResponse {
    private Long id;
    private String title;
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private List<TripMemberDTO> members;

    public TripDetailResponse() {}

    public static TripDetailResponse fromEntity(Trip trip, List<TripMemberDTO> members) {
        if (trip == null) return null;
        TripDetailResponse dto = new TripDetailResponse();
        dto.setId(trip.getId());
        dto.setTitle(trip.getTitle());
        dto.setDestination(trip.getDestination());
        dto.setStartDate(trip.getStartDate());
        dto.setEndDate(trip.getEndDate());
        dto.setCreatedAt(trip.getCreatedAt());
        if (trip.getCreatedBy() != null) {
            dto.setCreatedById(trip.getCreatedBy().getId());
            dto.setCreatedByName(trip.getCreatedBy().getName());
        }
        dto.setMembers(members);
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<TripMemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<TripMemberDTO> members) {
        this.members = members;
    }
}
