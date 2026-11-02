package com.tripsplit.trip.service;

import com.tripsplit.auth.entity.User;
import com.tripsplit.auth.repository.UserRepository;
import com.tripsplit.trip.dto.*;
import com.tripsplit.trip.entity.Trip;
import com.tripsplit.trip.entity.TripMember;
import com.tripsplit.trip.repository.TripMemberRepository;
import com.tripsplit.trip.repository.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final UserRepository userRepository;

    public TripService(TripRepository tripRepository,
                       TripMemberRepository tripMemberRepository,
                       UserRepository userRepository) {
        this.tripRepository = tripRepository;
        this.tripMemberRepository = tripMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("Trip end date cannot be earlier than start date");
        }

        User creator = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new IllegalArgumentException("Creator user not found with ID: " + request.getCreatedBy()));

        Trip trip = new Trip(
                request.getTitle().trim(),
                request.getDestination().trim(),
                request.getStartDate(),
                request.getEndDate(),
                creator
        );

        Trip savedTrip = tripRepository.save(trip);

        // Automatically add creator as the first member and ORGANIZER
        TripMember organizer = new TripMember(savedTrip, creator, "ORGANIZER");
        tripMemberRepository.save(organizer);

        return TripResponse.fromEntity(savedTrip, 1);
    }

    public List<TripResponse> getAllTrips() {
        return tripRepository.findAllByOrderByCreatedAtDesc().stream().map(trip -> {
            int count = tripMemberRepository.findByTripId(trip.getId()).size();
            return TripResponse.fromEntity(trip, count);
        }).collect(Collectors.toList());
    }

    public TripDetailResponse getTripDetail(Long tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with ID: " + tripId));

        List<TripMemberDTO> members = tripMemberRepository.findByTripId(tripId).stream()
                .map(TripMemberDTO::fromEntity)
                .collect(Collectors.toList());

        return TripDetailResponse.fromEntity(trip, members);
    }

    public Trip getTripEntity(Long tripId) {
        return tripRepository.findById(tripId)
                .orElseThrow(() -> new IllegalArgumentException("Trip not found with ID: " + tripId));
    }

    @Transactional
    public TripMemberDTO addMember(Long tripId, AddMemberRequest request) {
        Trip trip = getTripEntity(tripId);
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getUserId()));

        if (tripMemberRepository.existsByTripIdAndUserId(tripId, request.getUserId())) {
            throw new IllegalArgumentException("User is already a member of this trip");
        }

        TripMember member = new TripMember(trip, user, request.getRole());
        TripMember saved = tripMemberRepository.save(member);
        return TripMemberDTO.fromEntity(saved);
    }

    public List<TripMemberDTO> getTripMembers(Long tripId) {
        // verify trip exists
        getTripEntity(tripId);
        return tripMemberRepository.findByTripId(tripId).stream()
                .map(TripMemberDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
