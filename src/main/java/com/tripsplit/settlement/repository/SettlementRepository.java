package com.tripsplit.settlement.repository;

import com.tripsplit.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    List<Settlement> findByTripIdOrderBySettlementDateDescCreatedAtDesc(Long tripId);
}
