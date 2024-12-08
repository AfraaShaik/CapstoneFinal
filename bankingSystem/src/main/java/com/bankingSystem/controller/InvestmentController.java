package com.bankingSystem.controller;

import com.bankingSystem.DTO.InvestmentDTO;
import com.bankingSystem.DTO.InvestmentRequest;
import com.bankingSystem.entity.User;
import com.bankingSystem.service.InvestmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/investments")

public class InvestmentController {

    private final InvestmentService investmentService;

    public InvestmentController(InvestmentService investmentService) {
        this.investmentService = investmentService;
    }

    @PostMapping
    public ResponseEntity<InvestmentDTO> createInvestment(@RequestBody InvestmentRequest request, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        if (!"CUSTOMER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }

        InvestmentDTO created = investmentService.createInvestment(currentUser.getUserId(), request);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/me")
    public ResponseEntity<List<InvestmentDTO>> getMyInvestments(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        if (!"CUSTOMER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }

        List<InvestmentDTO> investments = investmentService.getInvestmentsForUser(currentUser.getUserId());
        return ResponseEntity.ok(investments);
    }

    @GetMapping
    public ResponseEntity<List<InvestmentDTO>> getAllInvestments(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }

        List<InvestmentDTO> allInvestments = investmentService.getAllInvestments();
        return ResponseEntity.ok(allInvestments);
    }

    @GetMapping("/market")
    public ResponseEntity<Map<String, Object>> getMarketTrends(Authentication auth) {
        Map<String, Object> trends = investmentService.getMarketTrends();
        return ResponseEntity.ok(trends);
    }
}
