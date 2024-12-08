package com.bankingSystem.controller;

import com.bankingSystem.DTO.GenerateReportRequest;
import com.bankingSystem.DTO.ReportDTO;
import com.bankingSystem.entity.User;
import com.bankingSystem.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")

public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<ReportDTO> generateReport(@RequestBody GenerateReportRequest request, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }

        ReportDTO report = reportService.generateReport(currentUser.getUserId(), request);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDTO> getReport(@PathVariable Long reportId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }

        ReportDTO report = reportService.getReportById(reportId);
        return ResponseEntity.ok(report);
    }

    @GetMapping
    public ResponseEntity<List<ReportDTO>> getAllReports(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }

        List<ReportDTO> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<ReportDTO>> getReportsByType(@PathVariable String type, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }

        List<ReportDTO> reports = reportService.getReportsByType(type);
        return ResponseEntity.ok(reports);
    }
}
