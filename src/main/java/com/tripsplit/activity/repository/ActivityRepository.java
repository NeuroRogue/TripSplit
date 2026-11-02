package com.tripsplit.activity.repository;

import com.tripsplit.activity.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByTripIdOrderByCreatedAtDesc(Long tripId);
}
