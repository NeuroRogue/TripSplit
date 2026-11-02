package com.tripsplit;

import com.tripsplit.common.MoneyUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TripSplitApplicationTests {

    @Test
    void testEqualSplitWithPaisePrecision() {
        // ₹1,000 split equally among 3 participants -> 333.34 + 333.33 + 333.33 = 1000.00
        BigDecimal total = new BigDecimal("1000.00");
        List<BigDecimal> shares = MoneyUtils.splitEqually(total, 3);

        assertEquals(3, shares.size());
        assertEquals(new BigDecimal("333.34"), shares.get(0));
        assertEquals(new BigDecimal("333.33"), shares.get(1));
        assertEquals(new BigDecimal("333.33"), shares.get(2));

        BigDecimal sum = shares.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(total, sum);
    }

    @Test
    void testEqualSplitFourParticipants() {
        BigDecimal total = new BigDecimal("24000.00");
        List<BigDecimal> shares = MoneyUtils.splitEqually(total, 4);

        assertEquals(4, shares.size());
        assertEquals(new BigDecimal("6000.00"), shares.get(0));
        assertEquals(new BigDecimal("6000.00"), shares.get(1));
        assertEquals(new BigDecimal("6000.00"), shares.get(2));
        assertEquals(new BigDecimal("6000.00"), shares.get(3));
    }
}

