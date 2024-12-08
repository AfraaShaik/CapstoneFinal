package com.bankingSystem.service;

import com.bankingSystem.DTO.GenerateReportRequest;
import com.bankingSystem.DTO.ReportDTO;
import com.bankingSystem.entity.Report;
import com.bankingSystem.mapper.ReportMapper;
import com.bankingSystem.repository.ReportRepository;
import com.bankingSystem.repository.TransactionRepository;
import com.bankingSystem.repository.LoanRepository;
import com.bankingSystem.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bankingSystem.entity.User;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final TransactionRepository transactionRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public ReportService(ReportRepository reportRepository,
                         TransactionRepository transactionRepository,
                         LoanRepository loanRepository,
                         UserRepository userRepository,
                         ObjectMapper objectMapper) {
        this.reportRepository = reportRepository;
        this.transactionRepository = transactionRepository;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ReportDTO generateReport(Long adminUserId, GenerateReportRequest request) {
        String type = request.getType();

        Map<String, Object> reportData;

        switch (type) {
            case "TRANSACTION_SUMMARY":
                // Example: total number of transactions, total transaction amount
                long transactionCount = transactionRepository.count();
                double totalAmount = transactionRepository.findAll()
                        .stream()
                        .mapToDouble(t -> t.getAmount())
                        .sum();
                reportData = Map.of(
                        "transactionCount", transactionCount,
                        "totalTransactionAmount", totalAmount
                );
                break;

            case "LOAN_SUMMARY":
                // Example: count of loans, count of approved loans
                long loanCount = loanRepository.count();
                long approvedLoans = loanRepository.findAll().stream()
                        .filter(loan -> "APPROVED".equalsIgnoreCase(loan.getStatus()))
                        .count();
                reportData = Map.of(
                        "totalLoans", loanCount,
                        "approvedLoans", approvedLoans
                );
                break;

            case "CUSTOMER_ACTIVITY":
                // Example: total number of users, active users
                long userCount = userRepository.count();
                long activeUsers = userRepository.findAll().stream()
                        .filter(User::isActive)
                        .count();
                reportData = Map.of(
                        "totalUsers", userCount,
                        "activeUsers", activeUsers
                );
                break;

            default:
                throw new RuntimeException("Unsupported report type: " + type);
        }

        // Convert reportData to JSON
        String dataJson;
        try {
            dataJson = objectMapper.writeValueAsString(reportData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate report data", e);
        }

        Report report = new Report();
        report.setType(type);
        report.setData(dataJson);
        report.setCreatedBy(adminUserId);

        Report saved = reportRepository.save(report);
        return ReportMapper.toDTO(saved);
    }

    public ReportDTO getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return ReportMapper.toDTO(report);
    }

    public List<ReportDTO> getAllReports() {
        return reportRepository.findAll().stream().map(ReportMapper::toDTO).collect(Collectors.toList());
    }

    public List<ReportDTO> getReportsByType(String type) {
        return reportRepository.findByType(type).stream().map(ReportMapper::toDTO).collect(Collectors.toList());
    }
}
