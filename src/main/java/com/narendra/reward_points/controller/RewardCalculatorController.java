package com.narendra.reward_points.controller;

import com.narendra.reward_points.model.Transaction;
import com.narendra.reward_points.service.RewardCalculatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class RewardCalculatorController {

    private final RewardCalculatorService rewardCalculatorService;

    // Constructor to inject RewardCalculatorService
    public RewardCalculatorController(RewardCalculatorService rewardCalculatorService) {
        this.rewardCalculatorService = rewardCalculatorService;
    }

    // POST endpoint to calculate rewards
    @PostMapping("/calculate")
    public ResponseEntity<Map<String, Map<java.time.Month, Integer>>> calculateRewards(@Valid @RequestBody List<Transaction> transactions) {
        // Call the service to calculate rewards
        Map<String, Map<java.time.Month, Integer>> rewards = rewardCalculatorService.calculateRewards(transactions);
        return ResponseEntity.ok(rewards);
    }
}
