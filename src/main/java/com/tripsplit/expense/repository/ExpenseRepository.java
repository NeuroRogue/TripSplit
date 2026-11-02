package com.tripsplit.expense.repository;

import com.tripsplit.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByTripIdOrderByExpenseDateDescCreatedAtDesc(Long tripId);
    List<Expense> findTop5ByOrderByCreatedAtDesc();
}
