package com.navi.education.model.entity;

import com.navi.education.model.enums.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnnualCommitmentTest {

    private Payment payment(String amount) {
        return Payment.builder()
                .amount(new BigDecimal(amount))
                .paymentDate(LocalDate.now())
                .paymentMethod(PaymentMethod.CASH)
                .build();
    }

    @Test
    void totalPaid_sumsAllPayments() {
        AnnualCommitment c = AnnualCommitment.builder()
                .annualAmount(new BigDecimal("100000"))
                .payments(List.of(payment("30000"), payment("20000")))
                .build();

        assertEquals(0, new BigDecimal("50000").compareTo(c.getTotalPaid()));
    }

    @Test
    void remainingBalance_isAmountMinusPaid() {
        AnnualCommitment c = AnnualCommitment.builder()
                .annualAmount(new BigDecimal("100000"))
                .payments(List.of(payment("40000")))
                .build();

        assertEquals(0, new BigDecimal("60000").compareTo(c.getRemainingBalance()));
        assertFalse(c.isFullyPaid());
    }

    @Test
    void isFullyPaid_whenBalanceZeroOrLess() {
        AnnualCommitment c = AnnualCommitment.builder()
                .annualAmount(new BigDecimal("100000"))
                .payments(List.of(payment("100000")))
                .build();

        assertTrue(c.isFullyPaid());
        assertEquals(0, BigDecimal.ZERO.compareTo(c.getRemainingBalance()));
    }
}
