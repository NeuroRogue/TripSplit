package com.tripsplit.activity.service;

import com.tripsplit.activity.dto.ActivityResponse;
import com.tripsplit.activity.dto.CreateActivityRequest;
import com.tripsplit.activity.entity.Activity;
import com.tripsplit.activity.repository.ActivityRepository;
import com.tripsplit.trip.entity.Trip;
import com.tripsplit.trip.service.TripService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final TripService tripService;

    public ActivityService(ActivityRepository activityRepository, TripService tripService) {
        this.activityRepository = activityRepository;
        this.tripService = tripService;
    }

    @Transactional
    public ActivityResponse createActivity(Long tripId, CreateActivityRequest request) {
        Trip trip = tripService.getTripEntity(tripId);
        Activity activity = new Activity(trip, request.getName().trim(), request.getCategory().trim(), request.getEstimatedCost());
        Activity saved = activityRepository.save(activity);
        return ActivityResponse.fromEntity(saved);
    }

    public List<ActivityResponse> getActivitiesByTrip(Long tripId) {
        tripService.getTripEntity(tripId);
        return activityRepository.findByTripIdOrderByCreatedAtDesc(tripId).stream()
                .map(ActivityResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ActivityResponse updateActivity(Long activityId, CreateActivityRequest request) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found with ID: " + activityId));

        activity.setName(request.getName().trim());
        activity.setCategory(request.getCategory().trim());
        if (request.getEstimatedCost() != null) {
            activity.setEstimatedCost(request.getEstimatedCost());
        }

        Activity updated = activityRepository.save(activity);
        return ActivityResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteActivity(Long activityId) {
        if (!activityRepository.existsById(activityId)) {
            throw new IllegalArgumentException("Activity not found with ID: " + activityId);
        }
        activityRepository.deleteById(activityId);
    }
}

