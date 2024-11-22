package com.narendra.reward_points.service;

import com.narendra.reward_points.model.Transaction;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.*;

@Service
public class RewardCalculatorService {

    // Validator initialization
    private final Validator validator;

    public RewardCalculatorService() {
        // Initialize Jakarta Validator
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public Map<String, Map<Month, Integer>> calculateRewards(List<Transaction> transactions) {
        // Map to store the rewards per customer per month
        Map<String, Map<Month, Integer>> customerRewards = new HashMap<>();

        // Validate each transaction
        for (Transaction transaction : transactions) {
            // Validate transaction
            Set<ConstraintViolation<Transaction>> violations = validator.validate(transaction);
            if (!violations.isEmpty()) {
                // Handle validation failures (e.g., throw exception, log error, etc.)
                throw new IllegalArgumentException("Invalid transaction data: " + violations);
            }

            // Calculate rewards
            int points = calculatePoints(transaction);

            // Update the rewards map for the customer and month
            customerRewards
                    .computeIfAbsent(transaction.getCustomerId(), k -> new HashMap<>())
                    .merge(transaction.getDate().getMonth(), points, Integer::sum);
        }

        return customerRewards;
    }

    private int calculatePoints(Transaction transaction) {
        double amount = transaction.getAmount();
        int points = 0;

        if (amount > 100) {
            points += 2 * (amount - 100); // 2 points for each dollar above $100
            amount = 100;
        }
        if (amount > 50) {
            points += (amount - 50); // 1 point for each dollar between $50 and $100
        }

        return points;
    }
}
