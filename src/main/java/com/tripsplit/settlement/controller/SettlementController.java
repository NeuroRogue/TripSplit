package com.tripsplit.settlement.controller;

import com.tripsplit.common.ApiResponse;
import com.tripsplit.settlement.dto.BalanceSummaryResponse;
import com.tripsplit.settlement.dto.CreateSettlementRequest;
import com.tripsplit.settlement.dto.SettlementResponse;
import com.tripsplit.settlement.service.SettlementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{tripId}")
@CrossOrigin(origins = "*")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @GetMapping("/balances")
    public ResponseEntity<ApiResponse<BalanceSummaryResponse>> getBalances(@PathVariable Long tripId) {
        BalanceSummaryResponse balances = settlementService.calculateBalances(tripId);
        return ResponseEntity.ok(ApiResponse.ok("Trip balances calculated successfully", balances));
    }

    @PostMapping("/settlements")
    public ResponseEntity<ApiResponse<SettlementResponse>> createSettlement(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateSettlementRequest request) {
        SettlementResponse settlement = settlementService.recordSettlement(tripId, request);
        return ResponseEntity.ok(ApiResponse.ok("Settlement recorded successfully", settlement));
    }

    @GetMapping("/settlements")
    public ResponseEntity<ApiResponse<List<SettlementResponse>>> getSettlements(@PathVariable Long tripId) {
        List<SettlementResponse> settlements = settlementService.getSettlementsByTrip(tripId);
        return ResponseEntity.ok(ApiResponse.ok("Settlements retrieved successfully", settlements));
    }
}
