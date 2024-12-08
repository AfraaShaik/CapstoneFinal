package com.bankingSystem.service;

import com.bankingSystem.DTO.InvestmentDTO;
import com.bankingSystem.DTO.InvestmentRequest;
import com.bankingSystem.entity.Investment;
import com.bankingSystem.mapper.InvestmentMapper;
import com.bankingSystem.repository.InvestmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InvestmentService {
    private final InvestmentRepository investmentRepository;
    private final NotificationService notificationService;

    public InvestmentService(InvestmentRepository investmentRepository, NotificationService notificationService) {
        this.investmentRepository = investmentRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public InvestmentDTO createInvestment(Long userId, InvestmentRequest request) {
        Investment investment = new Investment();
        investment.setUserId(userId);
        investment.setInvestmentType(request.getInvestmentType());
        investment.setAmount(request.getAmount());
        investment.setMaturityDate(request.getMaturityDate());
        // startDate and status set in @PrePersist

        Investment saved = investmentRepository.save(investment);

        if (notificationService.isNotificationAllowed(userId, "ACCOUNT_ACTIVITY")) {
            notificationService.createNotification(userId, "ACCOUNT_ACTIVITY",
                    "You have created a new " + saved.getInvestmentType() + " investment of amount " + saved.getAmount());
        }
        return InvestmentMapper.toDTO(saved);
    }

    public List<InvestmentDTO> getInvestmentsForUser(Long userId) {
        return investmentRepository.findByUserId(userId)
                .stream().map(InvestmentMapper::toDTO).collect(Collectors.toList());
    }

    public List<InvestmentDTO> getAllInvestments() {
        return investmentRepository.findAll()
                .stream().map(InvestmentMapper::toDTO).collect(Collectors.toList());
    }

    // Mock Market Trends and Interest Rates
    // In reality, this data could come from a third-party API or another service.
    public Map<String, Object> getMarketTrends() {
        // Just an example:
        return Map.of(
                "interestRates", Map.of(
                        "FIXED_DEPOSIT", 5.5,
                        "MUTUAL_FUND", 7.2,
                        "BOND", 4.0
                ),
                "marketTrends", Map.of(
                        "FIXED_DEPOSIT", "Stable",
                        "MUTUAL_FUND", "Rising",
                        "BOND", "Stable"
                )
        );
    }
}
