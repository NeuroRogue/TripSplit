package com.tripsplit.activity.controller;

import com.tripsplit.activity.dto.ActivityResponse;
import com.tripsplit.activity.dto.CreateActivityRequest;
import com.tripsplit.activity.service.ActivityService;
import com.tripsplit.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/trips/{tripId}/activities")
    public ResponseEntity<ApiResponse<List<ActivityResponse>>> getActivities(@PathVariable Long tripId) {
        List<ActivityResponse> activities = activityService.getActivitiesByTrip(tripId);
        return ResponseEntity.ok(ApiResponse.ok("Activities retrieved successfully", activities));
    }

    @PostMapping("/trips/{tripId}/activities")
    public ResponseEntity<ApiResponse<ActivityResponse>> createActivity(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateActivityRequest request) {
        ActivityResponse activity = activityService.createActivity(tripId, request);
        return ResponseEntity.ok(ApiResponse.ok("Activity created successfully", activity));
    }

    @PutMapping("/activities/{activityId}")
    public ResponseEntity<ApiResponse<ActivityResponse>> updateActivity(
            @PathVariable Long activityId,
            @Valid @RequestBody CreateActivityRequest request) {
        ActivityResponse activity = activityService.updateActivity(activityId, request);
        return ResponseEntity.ok(ApiResponse.ok("Activity updated successfully", activity));
    }

    @DeleteMapping("/activities/{activityId}")
    public ResponseEntity<ApiResponse<String>> deleteActivity(@PathVariable Long activityId) {
        activityService.deleteActivity(activityId);
        return ResponseEntity.ok(ApiResponse.ok("Activity deleted successfully", "Activity " + activityId + " removed"));
    }
}

