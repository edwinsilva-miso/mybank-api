package com.mybank.domains.transaction.domain.valueobject;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class MoneyTest {

    @Test
    void createMoney_ValidAmount_Success() {
        // Given
        BigDecimal amount = new BigDecimal("100.50");

        // When
        Money money = new Money(amount);

        // Then
        assertEquals(new BigDecimal("100.50"), money.getAmount());
        assertEquals(Currency.getInstance("USD"), money.getCurrency());
    }

    @Test
    void createMoney_AmountWithMoreThanTwoDecimals_RoundsToTwoDecimals() {
        // Given
        BigDecimal amount = new BigDecimal("100.567");

        // When
        Money money = new Money(amount);

        // Then
        assertEquals(new BigDecimal("100.57"), money.getAmount());
    }

    @Test
    void createMoney_NullAmount_ThrowsException() {
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            new Money(null);
        });
    }

    @Test
    void addMoney_Success() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"));
        Money money2 = new Money(new BigDecimal("50.25"));

        // When
        Money result = money1.add(money2);

        // Then
        assertEquals(new BigDecimal("150.75"), result.getAmount());
        assertEquals(Currency.getInstance("USD"), result.getCurrency());
    }

    @Test
    void addMoney_DifferentCurrencies_ThrowsException() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"));
        Money money2 = new Money(new BigDecimal("50.25"), java.util.Currency.getInstance("EUR"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            money1.add(money2);
        });
    }

    @Test
    void subtractMoney_Success() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"));
        Money money2 = new Money(new BigDecimal("30.25"));

        // When
        Money result = money1.subtract(money2);

        // Then
        assertEquals(new BigDecimal("70.25"), result.getAmount());
        assertEquals(Currency.getInstance("USD"), result.getCurrency());
    }

    @Test
    void subtractMoney_ResultNegative_ThrowsException() {
        // Given
        Money money1 = new Money(new BigDecimal("50.00"));
        Money money2 = new Money(new BigDecimal("100.00"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            money1.subtract(money2);
        });
    }

    @Test
    void multiplyMoney_Success() {
        // Given
        Money money = new Money(new BigDecimal("10.50"));
        BigDecimal multiplier = new BigDecimal("3");

        // When
        Money result = money.multiply(multiplier);

        // Then
        assertEquals(new BigDecimal("31.50"), result.getAmount());
        assertEquals(Currency.getInstance("USD"), result.getCurrency());
    }

    @Test
    void multiplyMoney_NegativeMultiplier_ThrowsException() {
        // Given
        Money money = new Money(new BigDecimal("10.50"));
        BigDecimal multiplier = new BigDecimal("-1");

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            money.multiply(multiplier);
        });
    }

    @Test
    void isGreaterThan_Success() {
        // Given
        Money money1 = new Money(new BigDecimal("100.00"));
        Money money2 = new Money(new BigDecimal("50.00"));

        // When & Then
        assertTrue(money1.isGreaterThan(money2));
        assertFalse(money2.isGreaterThan(money1));
    }

    @Test
    void isLessThan_Success() {
        // Given
        Money money1 = new Money(new BigDecimal("50.00"));
        Money money2 = new Money(new BigDecimal("100.00"));

        // When & Then
        assertTrue(money1.isLessThan(money2));
        assertFalse(money2.isLessThan(money1));
    }

    @Test
    void isEqualTo_Success() {
        // Given
        Money money1 = new Money(new BigDecimal("100.00"));
        Money money2 = new Money(new BigDecimal("100.00"));

        // When & Then
        assertEquals(money1, money2);
    }

    @Test
    void isZero_Success() {
        // Given
        Money zeroMoney = new Money(BigDecimal.ZERO);
        Money nonZeroMoney = new Money(new BigDecimal("100.00"));

        // When & Then
        assertTrue(zeroMoney.isZero());
        assertFalse(nonZeroMoney.isZero());
    }

    @Test
    void isPositive_Success() {
        // Given
        Money positiveMoney = new Money(new BigDecimal("100.00"));
        Money zeroMoney = new Money(BigDecimal.ZERO);

        // When & Then
        assertTrue(positiveMoney.isPositive());
        assertFalse(zeroMoney.isPositive());
    }

    @Test
    void toString_Success() {
        // Given
        Money money = new Money(new BigDecimal("100.50"));

        // When
        String result = money.toString();

        // Then
        assertEquals("$ 100.50", result);
    }

    @Test
    void equals_SameValues_ReturnsTrue() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"));
        Money money2 = new Money(new BigDecimal("100.50"));

        // When & Then
        assertEquals(money1, money2);
    }

    @Test
    void equals_DifferentValues_ReturnsFalse() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"));
        Money money2 = new Money(new BigDecimal("200.50"));

        // When & Then
        assertNotEquals(money1, money2);
    }

    @Test
    void hashCode_SameValues_SameHashCode() {
        // Given
        Money money1 = new Money(new BigDecimal("100.50"));
        Money money2 = new Money(new BigDecimal("100.50"));

        // When & Then
        assertEquals(money1.hashCode(), money2.hashCode());
    }
} 