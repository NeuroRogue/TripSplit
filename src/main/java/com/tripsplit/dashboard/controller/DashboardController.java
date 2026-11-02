package com.tripsplit.dashboard.controller;

import com.tripsplit.common.ApiResponse;
import com.tripsplit.dashboard.dto.DashboardSummaryResponse;
import com.tripsplit.dashboard.dto.TripSummaryResponse;
import com.tripsplit.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getOverallSummary() {
        DashboardSummaryResponse summary = dashboardService.getOverallSummary();
        return ResponseEntity.ok(ApiResponse.ok("Overall dashboard summary retrieved", summary));
    }

    @GetMapping("/trips/{tripId}/summary")
    public ResponseEntity<ApiResponse<TripSummaryResponse>> getTripSummary(@PathVariable Long tripId) {
        TripSummaryResponse summary = dashboardService.getTripSummary(tripId);
        return ResponseEntity.ok(ApiResponse.ok("Trip summary retrieved", summary));
    }
}

