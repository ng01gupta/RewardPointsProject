package com.narendra.reward_points;

import static org.junit.jupiter.api.Assertions.*;

import com.narendra.reward_points.model.Transaction;
import com.narendra.reward_points.service.RewardCalculatorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RewardCalculatorServiceTest {

	private RewardCalculatorService rewardCalculatorService;
	private Validator validator;

	@BeforeEach
	void setUp() {
		// Initialize RewardCalculatorService
		rewardCalculatorService = new RewardCalculatorService();

		// Initialize validator using Jakarta Validation API
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Test
	void testCalculateRewards_ValidTransaction() {
		// Valid transaction for a customer
		Transaction transaction = new Transaction("C123", 120.0, LocalDate.of(2023, 9, 1));

		// Calculate reward points
		Map<String, Map<Month, Integer>> rewards = rewardCalculatorService.calculateRewards(List.of(transaction));

		// Assert that rewards are calculated correctly
		assertNotNull(rewards);
		assertTrue(rewards.containsKey("C123"));
		Map<Month, Integer> customerRewards = rewards.get("C123");
		assertEquals(90, customerRewards.get(Month.SEPTEMBER), "Expected reward points for September");
	}

	@Test
	void testCalculateRewards_MultipleTransactions() {
		// Multiple transactions for a customer
		List<Transaction> transactions = List.of(
				new Transaction("C123", 120.0, LocalDate.of(2023, 9, 1)),
				new Transaction("C123", 80.0, LocalDate.of(2023, 9, 15))
		);

		// Calculate reward points
		Map<String, Map<Month, Integer>> rewards = rewardCalculatorService.calculateRewards(transactions);

		// Assert that rewards are calculated correctly
		assertNotNull(rewards);
		assertTrue(rewards.containsKey("C123"));

		Map<Month, Integer> customerRewards = rewards.get("C123");
		assertEquals(120, customerRewards.get(Month.SEPTEMBER), "Expected reward points for September");
	}

	@Test
	void testCalculateRewards_InvalidAmount() {
		// Transaction with invalid amount (negative)
		Transaction transaction = new Transaction("C123", -10.0, LocalDate.now());

		// Validate the transaction
		Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);

		// Assert validation failure
		assertFalse(violations.isEmpty(), "Validation should fail for negative amount");

		// Assert specific violation message
		assertTrue(
				violations.stream().anyMatch(v -> v.getMessage().equals("Amount must be greater than zero")),
				"Expected validation message for amount"
		);
	}

	@Test
	void testCalculateRewards_NoTransactions() {
		// No transactions provided
		List<Transaction> transactions = List.of();

		// Calculate rewards (should return an empty result)
		Map<String, Map<Month, Integer>> rewards = rewardCalculatorService.calculateRewards(transactions);

		// Assert that no rewards are calculated
		assertNotNull(rewards);
		assertTrue(rewards.isEmpty(), "Rewards should be empty for no transactions");
	}

	@Test
	void testCalculateRewards_AmountBelowThreshold() {
		// Transaction with amount below $50 (should only get 1 point per dollar for the amount between $50 and $100)
		Transaction transaction = new Transaction("C123", 60.0, LocalDate.of(2023, 9, 1));

		// Calculate reward points
		Map<String, Map<Month, Integer>> rewards = rewardCalculatorService.calculateRewards(List.of(transaction));

		// Assert that the correct reward points are calculated (1 point for each dollar between $50 and $60)
		assertNotNull(rewards);
		assertTrue(rewards.containsKey("C123"));
		Map<Month, Integer> customerRewards = rewards.get("C123");
		assertEquals(10, customerRewards.get(Month.SEPTEMBER), "Expected reward points for September");
	}

	@Test
	void testCalculateRewards_AmountAboveThreshold() {
		// Transaction with amount above $100 (should get 2 points for every dollar over $100 and 1 point for each dollar between $50 and $100)
		Transaction transaction = new Transaction("C123", 150.0, LocalDate.of(2023, 9, 1));

		// Calculate reward points
		Map<String, Map<Month, Integer>> rewards = rewardCalculatorService.calculateRewards(List.of(transaction));

		// Assert that the correct reward points are calculated (2 points for $50 above $100, 1 point for $50 between $50 and $100)
		assertNotNull(rewards);
		assertTrue(rewards.containsKey("C123"));
		Map<Month, Integer> customerRewards = rewards.get("C123");
		assertEquals(150, customerRewards.get(Month.SEPTEMBER), "Expected reward points for September");
	}

	@Test
	void testCalculateRewards_MultipleMonths() {
		// Multiple transactions in different months
		List<Transaction> transactions = List.of(
				new Transaction("C123", 120.0, LocalDate.of(2023, 9, 1)),
				new Transaction("C123", 80.0, LocalDate.of(2023, 10, 1)),
				new Transaction("C123", 150.0, LocalDate.of(2023, 10, 15)),
				new Transaction("C123", 100.0, LocalDate.of(2023, 11, 1))
		);

		// Calculate reward points
		Map<String, Map<Month, Integer>> rewards = rewardCalculatorService.calculateRewards(transactions);

		// Assert that the reward points are calculated for each month
		assertNotNull(rewards);
		assertTrue(rewards.containsKey("C123"));
		Map<Month, Integer> customerRewards = rewards.get("C123");

		// September
		assertEquals(90, customerRewards.get(Month.SEPTEMBER), "Expected reward points for September");
		// October
		assertEquals(180, customerRewards.get(Month.OCTOBER), "Expected reward points for October");
		// November
		assertEquals(50, customerRewards.get(Month.NOVEMBER), "Expected reward points for November");
	}
}
