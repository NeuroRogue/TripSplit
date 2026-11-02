package com.tripsplit.trip.controller;

import com.tripsplit.common.ApiResponse;
import com.tripsplit.trip.dto.*;
import com.tripsplit.trip.service.TripService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@CrossOrigin(origins = "*")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TripResponse>>> getAllTrips() {
        List<TripResponse> trips = tripService.getAllTrips();
        return ResponseEntity.ok(ApiResponse.ok("Trips retrieved successfully", trips));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TripResponse>> createTrip(@Valid @RequestBody CreateTripRequest request) {
        TripResponse trip = tripService.createTrip(request);
        return ResponseEntity.ok(ApiResponse.ok("Trip created successfully", trip));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<ApiResponse<TripDetailResponse>> getTripById(@PathVariable Long tripId) {
        TripDetailResponse trip = tripService.getTripDetail(tripId);
        return ResponseEntity.ok(ApiResponse.ok("Trip details retrieved successfully", trip));
    }

    @PostMapping("/{tripId}/members")
    public ResponseEntity<ApiResponse<TripMemberDTO>> addMember(
            @PathVariable Long tripId,
            @Valid @RequestBody AddMemberRequest request) {
        TripMemberDTO member = tripService.addMember(tripId, request);
        return ResponseEntity.ok(ApiResponse.ok("Member added successfully", member));
    }

    @GetMapping("/{tripId}/members")
    public ResponseEntity<ApiResponse<List<TripMemberDTO>>> getMembers(@PathVariable Long tripId) {
        List<TripMemberDTO> members = tripService.getTripMembers(tripId);
        return ResponseEntity.ok(ApiResponse.ok("Trip members retrieved successfully", members));
    }
}
