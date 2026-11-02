package com.tripsplit.expense.service;

import com.tripsplit.activity.entity.Activity;
import com.tripsplit.activity.repository.ActivityRepository;
import com.tripsplit.auth.entity.User;
import com.tripsplit.auth.repository.UserRepository;
import com.tripsplit.common.MoneyUtils;
import com.tripsplit.expense.dto.CreateExpenseRequest;
import com.tripsplit.expense.dto.ExpenseResponse;
import com.tripsplit.expense.entity.Expense;
import com.tripsplit.expense.entity.ExpenseShare;
import com.tripsplit.expense.repository.ExpenseRepository;
import com.tripsplit.expense.repository.ExpenseShareRepository;
import com.tripsplit.trip.entity.Trip;
import com.tripsplit.trip.repository.TripMemberRepository;
import com.tripsplit.trip.service.TripService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseShareRepository expenseShareRepository;
    private final TripService tripService;
    private final TripMemberRepository tripMemberRepository;
    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          ExpenseShareRepository expenseShareRepository,
                          TripService tripService,
                          TripMemberRepository tripMemberRepository,
                          UserRepository userRepository,
                          ActivityRepository activityRepository) {
        this.expenseRepository = expenseRepository;
        this.expenseShareRepository = expenseShareRepository;
        this.tripService = tripService;
        this.tripMemberRepository = tripMemberRepository;
        this.userRepository = userRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public ExpenseResponse recordExpense(Long tripId, CreateExpenseRequest request) {
        Trip trip = tripService.getTripEntity(tripId);

        // 1. Validate payer belongs to the trip
        if (!tripMemberRepository.existsByTripIdAndUserId(tripId, request.getPayerId())) {
            throw new IllegalArgumentException("Payer (ID: " + request.getPayerId() + ") is not a registered member of trip " + tripId);
        }
        User payer = userRepository.findById(request.getPayerId())
                .orElseThrow(() -> new IllegalArgumentException("Payer user not found with ID: " + request.getPayerId()));

        // 2. Validate participants belong to the trip
        Set<Long> uniqueParticipantIds = new HashSet<>(request.getParticipantUserIds());
        if (uniqueParticipantIds.isEmpty()) {
            throw new IllegalArgumentException("At least one participant is required to share the expense");
        }

        List<User> participants = new ArrayList<>();
        for (Long pId : uniqueParticipantIds) {
            if (!tripMemberRepository.existsByTripIdAndUserId(tripId, pId)) {
                throw new IllegalArgumentException("Participant with user ID " + pId + " does not belong to trip " + tripId);
            }
            User participant = userRepository.findById(pId)
                    .orElseThrow(() -> new IllegalArgumentException("Participant user not found with ID: " + pId));
            participants.add(participant);
        }

        // 3. Optional activity validation
        Activity activity = null;
        if (request.getActivityId() != null) {
            activity = activityRepository.findById(request.getActivityId())
                    .orElseThrow(() -> new IllegalArgumentException("Activity not found with ID: " + request.getActivityId()));
            if (!activity.getTrip().getId().equals(tripId)) {
                throw new IllegalArgumentException("Activity ID " + request.getActivityId() + " does not belong to trip " + tripId);
            }
        }

        // 4. Create and persist Expense
        Expense expense = new Expense(
                trip,
                activity,
                request.getTitle().trim(),
                request.getAmount(),
                payer,
                request.getExpenseDate(),
                request.getCategory()
        );
        Expense savedExpense = expenseRepository.save(expense);

        // 5. Calculate equal split in paise with deterministic remainder assignment
        List<BigDecimal> shares = MoneyUtils.splitEqually(request.getAmount(), participants.size());

        List<ExpenseShare> expenseShares = new ArrayList<>();
        for (int i = 0; i < participants.size(); i++) {
            ExpenseShare share = new ExpenseShare(savedExpense, participants.get(i), shares.get(i));
            expenseShares.add(share);
        }

        List<ExpenseShare> savedShares = expenseShareRepository.saveAll(expenseShares);
        savedExpense.setShares(savedShares);

        return ExpenseResponse.fromEntity(savedExpense);
    }

    public List<ExpenseResponse> getExpensesByTrip(Long tripId) {
        tripService.getTripEntity(tripId);
        return expenseRepository.findByTripIdOrderByExpenseDateDescCreatedAtDesc(tripId).stream()
                .map(ExpenseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ExpenseResponse getExpenseById(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new IllegalArgumentException("Expense not found with ID: " + expenseId));
        return ExpenseResponse.fromEntity(expense);
    }

    public List<Expense> getRawExpensesByTrip(Long tripId) {
        return expenseRepository.findByTripIdOrderByExpenseDateDescCreatedAtDesc(tripId);
    }
}
