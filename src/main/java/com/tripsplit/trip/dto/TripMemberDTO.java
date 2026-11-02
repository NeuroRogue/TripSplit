package com.tripsplit.trip.dto;

import com.tripsplit.trip.entity.TripMember;
import java.time.LocalDateTime;

public class TripMemberDTO {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String role;
    private LocalDateTime joinedAt;

    public TripMemberDTO() {}

    public static TripMemberDTO fromEntity(TripMember member) {
        if (member == null) return null;
        TripMemberDTO dto = new TripMemberDTO();
        dto.setId(member.getId());
        dto.setRole(member.getRole());
        dto.setJoinedAt(member.getJoinedAt());
        if (member.getUser() != null) {
            dto.setUserId(member.getUser().getId());
            dto.setName(member.getUser().getName());
            dto.setEmail(member.getUser().getEmail());
            dto.setPhone(member.getUser().getPhone());
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}

