package com.tripsplit.settlement.service;

import com.tripsplit.auth.entity.User;
import com.tripsplit.auth.repository.UserRepository;
import com.tripsplit.common.MoneyUtils;
import com.tripsplit.expense.entity.Expense;
import com.tripsplit.expense.entity.ExpenseShare;
import com.tripsplit.expense.service.ExpenseService;
import com.tripsplit.settlement.dto.*;
import com.tripsplit.settlement.entity.Settlement;
import com.tripsplit.settlement.repository.SettlementRepository;
import com.tripsplit.trip.dto.TripMemberDTO;
import com.tripsplit.trip.entity.Trip;
import com.tripsplit.trip.repository.TripMemberRepository;
import com.tripsplit.trip.service.TripService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final ExpenseService expenseService;
    private final TripService tripService;
    private final TripMemberRepository tripMemberRepository;
    private final UserRepository userRepository;

    public SettlementService(SettlementRepository settlementRepository,
                             ExpenseService expenseService,
                             TripService tripService,
                             TripMemberRepository tripMemberRepository,
                             UserRepository userRepository) {
        this.settlementRepository = settlementRepository;
        this.expenseService = expenseService;
        this.tripService = tripService;
        this.tripMemberRepository = tripMemberRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SettlementResponse recordSettlement(Long tripId, CreateSettlementRequest request) {
        Trip trip = tripService.getTripEntity(tripId);

        if (request.getPayerId().equals(request.getPayeeId())) {
            throw new IllegalArgumentException("Payer and Payee cannot be the same person");
        }

        if (!tripMemberRepository.existsByTripIdAndUserId(tripId, request.getPayerId())) {
            throw new IllegalArgumentException("Payer (ID: " + request.getPayerId() + ") is not a member of trip " + tripId);
        }

        if (!tripMemberRepository.existsByTripIdAndUserId(tripId, request.getPayeeId())) {
            throw new IllegalArgumentException("Payee (ID: " + request.getPayeeId() + ") is not a member of trip " + tripId);
        }

        User payer = userRepository.findById(request.getPayerId())
                .orElseThrow(() -> new IllegalArgumentException("Payer user not found with ID: " + request.getPayerId()));
        User payee = userRepository.findById(request.getPayeeId())
                .orElseThrow(() -> new IllegalArgumentException("Payee user not found with ID: " + request.getPayeeId()));

        Settlement settlement = new Settlement(
                trip,
                payer,
                payee,
                MoneyUtils.round(request.getAmount()),
                request.getSettlementDate(),
                request.getNotes()
        );

        Settlement saved = settlementRepository.save(settlement);
        return SettlementResponse.fromEntity(saved);
    }

    public List<SettlementResponse> getSettlementsByTrip(Long tripId) {
        tripService.getTripEntity(tripId);
        return settlementRepository.findByTripIdOrderBySettlementDateDescCreatedAtDesc(tripId).stream()
                .map(SettlementResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public BalanceSummaryResponse calculateBalances(Long tripId) {
        Trip trip = tripService.getTripEntity(tripId);
        List<TripMemberDTO> members = tripService.getTripMembers(tripId);
        List<Expense> expenses = expenseService.getRawExpensesByTrip(tripId);
        List<Settlement> settlements = settlementRepository.findByTripIdOrderBySettlementDateDescCreatedAtDesc(tripId);

        BigDecimal totalTripExpense = BigDecimal.ZERO;

        // Map for tracking amounts in paise
        Map<Long, Long> paidInExpensesMap = new HashMap<>();
        Map<Long, Long> owedInSharesMap = new HashMap<>();
        Map<Long, Long> paidInSettlementsMap = new HashMap<>();
        Map<Long, Long> receivedInSettlementsMap = new HashMap<>();
        Map<Long, String> userNamesMap = new HashMap<>();

        for (TripMemberDTO m : members) {
            paidInExpensesMap.put(m.getUserId(), 0L);
            owedInSharesMap.put(m.getUserId(), 0L);
            paidInSettlementsMap.put(m.getUserId(), 0L);
            receivedInSettlementsMap.put(m.getUserId(), 0L);
            userNamesMap.put(m.getUserId(), m.getName());
        }

        for (Expense exp : expenses) {
            totalTripExpense = totalTripExpense.add(exp.getAmount());
            long expPaise = toPaise(exp.getAmount());
            Long payerId = exp.getPayer().getId();
            paidInExpensesMap.put(payerId, paidInExpensesMap.getOrDefault(payerId, 0L) + expPaise);

            if (exp.getShares() != null) {
                for (ExpenseShare share : exp.getShares()) {
                    Long shareUserId = share.getUser().getId();
                    long sharePaise = toPaise(share.getShareAmount());
                    owedInSharesMap.put(shareUserId, owedInSharesMap.getOrDefault(shareUserId, 0L) + sharePaise);
                }
            }
        }

        for (Settlement s : settlements) {
            long setPaise = toPaise(s.getAmount());
            Long payerId = s.getPayer().getId();
            Long payeeId = s.getPayee().getId();
            paidInSettlementsMap.put(payerId, paidInSettlementsMap.getOrDefault(payerId, 0L) + setPaise);
            receivedInSettlementsMap.put(payeeId, receivedInSettlementsMap.getOrDefault(payeeId, 0L) + setPaise);
        }

        List<UserBalanceDTO> userBalances = new ArrayList<>();
        // For simplified debts calculation
        List<PersonBalance> debtors = new ArrayList<>();
        List<PersonBalance> creditors = new ArrayList<>();

        for (TripMemberDTO m : members) {
            Long uId = m.getUserId();
            long paid = paidInExpensesMap.getOrDefault(uId, 0L);
            long owed = owedInSharesMap.getOrDefault(uId, 0L);
            long sPaid = paidInSettlementsMap.getOrDefault(uId, 0L);
            long sRecv = receivedInSettlementsMap.getOrDefault(uId, 0L);

            long netPaise = (paid - owed) + (sPaid - sRecv);

            userBalances.add(new UserBalanceDTO(
                    uId,
                    m.getName(),
                    fromPaise(paid),
                    fromPaise(owed),
                    fromPaise(sPaid),
                    fromPaise(sRecv),
                    fromPaise(netPaise)
            ));

            if (netPaise < 0) {
                debtors.add(new PersonBalance(uId, m.getName(), -netPaise));
            } else if (netPaise > 0) {
                creditors.add(new PersonBalance(uId, m.getName(), netPaise));
            }
        }

        List<DebtRelationDTO> simplifiedDebts = simplifyDebts(debtors, creditors);

        return new BalanceSummaryResponse(
                tripId,
                MoneyUtils.round(totalTripExpense),
                userBalances,
                simplifiedDebts
        );
    }

    private static class PersonBalance {
        Long userId;
        String name;
        long amountPaise;

        PersonBalance(Long userId, String name, long amountPaise) {
            this.userId = userId;
            this.name = name;
            this.amountPaise = amountPaise;
        }
    }

    private List<DebtRelationDTO> simplifyDebts(List<PersonBalance> debtors, List<PersonBalance> creditors) {
        List<DebtRelationDTO> debts = new ArrayList<>();
        int dIdx = 0;
        int cIdx = 0;

        while (dIdx < debtors.size() && cIdx < creditors.size()) {
            PersonBalance debtor = debtors.get(dIdx);
            PersonBalance creditor = creditors.get(cIdx);

            long settledAmount = Math.min(debtor.amountPaise, creditor.amountPaise);

            if (settledAmount > 0) {
                debts.add(new DebtRelationDTO(
                        debtor.userId,
                        debtor.name,
                        creditor.userId,
                        creditor.name,
                        fromPaise(settledAmount)
                ));
            }

            debtor.amountPaise -= settledAmount;
            creditor.amountPaise -= settledAmount;

            if (debtor.amountPaise == 0) {
                dIdx++;
            }
            if (creditor.amountPaise == 0) {
                cIdx++;
            }
        }

        return debts;
    }

    private long toPaise(BigDecimal amount) {
        if (amount == null) return 0L;
        return amount.setScale(2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).longValueExact();
    }

    private BigDecimal fromPaise(long paise) {
        return BigDecimal.valueOf(paise, 2);
    }
}

