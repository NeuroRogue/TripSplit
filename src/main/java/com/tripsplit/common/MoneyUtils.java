package com.tripsplit.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * MoneyUtils ensures accurate financial calculations for splitting expenses.
 * Avoids floating-point rounding issues by computing in integer paise (minor units)
 * and distributing remaining paise deterministically.
 */
public final class MoneyUtils {

    private MoneyUtils() {}

    /**
     * Splits totalAmount equally among numberOfParticipants.
     * Guarantees that sum(shares) == totalAmount exactly.
     *
     * Example: ₹1000 among 3 participants
     * Total paise = 100000
     * Base share = 100000 / 3 = 33333 paise (₹333.33)
     * Remainder = 100000 % 3 = 1 paise
     * Result = [₹333.34, ₹333.33, ₹333.33]
     */
    public static List<BigDecimal> splitEqually(BigDecimal totalAmount, int numberOfParticipants) {
        if (numberOfParticipants <= 0) {
            throw new IllegalArgumentException("Number of participants must be greater than zero");
        }
        if (totalAmount == null || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than zero");
        }

        // Convert to paise (cents) as long integer
        long totalPaise = totalAmount.setScale(2, RoundingMode.HALF_UP)
                                     .multiply(BigDecimal.valueOf(100))
                                     .longValueExact();

        long basePaise = totalPaise / numberOfParticipants;
        long remainderPaise = totalPaise % numberOfParticipants;

        List<BigDecimal> shares = new ArrayList<>(numberOfParticipants);
        for (int i = 0; i < numberOfParticipants; i++) {
            long participantPaise = basePaise + (i < remainderPaise ? 1 : 0);
            shares.add(BigDecimal.valueOf(participantPaise, 2));
        }

        return shares;
    }

    public static BigDecimal round(BigDecimal value) {
        return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
    }
}
