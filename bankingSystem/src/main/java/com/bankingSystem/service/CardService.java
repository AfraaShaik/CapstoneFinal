package com.bankingSystem.service;

import com.bankingSystem.DTO.CardDTO;
import com.bankingSystem.DTO.CardRequest;
import com.bankingSystem.entity.Card;
import com.bankingSystem.mapper.CardMapper;
import com.bankingSystem.repository.CardRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public CardService(CardRepository cardRepository, PasswordEncoder passwordEncoder, NotificationService notificationService) {
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @Transactional
    public CardDTO applyForCard(Long userId, CardRequest request) {
        // Generate the card details
        String cardNumber = generateCardNumber();
        String cvv = generateCVV();
        LocalDate expiryDate = generateExpiryDate();

        // Create a new card object
        Card card = new Card();
        card.setUserId(userId);
        card.setCardType(request.getCardType());
        card.setCardNumber(cardNumber);
        card.setExpiryDate(expiryDate);

        // Hash CVV before saving
        String cvvHash = passwordEncoder.encode(cvv);
        card.setCvvHash(cvvHash);

        card.setStatus("PENDING"); // Card starts as "PENDING"

        // Save the card to the database
        Card saved = cardRepository.save(card);

        // Send notification (if applicable)
        if (notificationService.isNotificationAllowed(userId, "CARD_ACTIVITY")) {
            notificationService.createNotification(userId, "CARD_ACTIVITY",
                    "Your card application (" + saved.getCardType() + ") is pending approval.");
        }

        return CardMapper.toDTO(saved);
    }

    private String generateCardNumber() {
        Random random = new Random();
        long cardNumber = 4000000000000000L + random.nextLong(100000000000000L);
        return String.valueOf(cardNumber);
    }

    private String generateCVV() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));  // Generates a 3-digit CVV
    }

    private LocalDate generateExpiryDate() {
        // Set expiry date to 3 years from now
        return LocalDate.now().plusYears(3);
    }

    public List<CardDTO> getCardsForUser(Long userId) {
        return cardRepository.findByUserId(userId)
                .stream()
                .map(CardMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CardDTO blockCard(Long userId, Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new RuntimeException("Card not found"));
        if (!card.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to block this card");
        }
        card.setStatus("BLOCKED");
        Card updated = cardRepository.save(card);
        if (notificationService.isNotificationAllowed(userId, "CARD_ACTIVITY")) {
            notificationService.createNotification(userId, "CARD_ACTIVITY",
                    "Your card (ID: " + cardId + ") has been blocked.");
        }
        return CardMapper.toDTO(updated);
    }

    @Transactional
    public CardDTO reportLostOrStolen(Long userId, Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new RuntimeException("Card not found"));
        if (!card.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to report this card");
        }
        card.setStatus("LOST");
        Card updated = cardRepository.save(card);
        return CardMapper.toDTO(updated);
    }

    @Transactional
    public CardDTO replaceCard(Long userId, Long oldCardId, CardRequest newCardRequest) {
        Card oldCard = cardRepository.findById(oldCardId).orElseThrow(() -> new RuntimeException("Card not found"));
        if (!oldCard.getUserId().equals(userId)) {
            throw new RuntimeException("Not authorized to replace this card");
        }
        oldCard.setStatus("REPLACED");
        cardRepository.save(oldCard);

        // Create a new card as replacement (PENDING approval again)
        return applyForCard(userId, newCardRequest);
    }

    // ADMIN functionalities
    public List<CardDTO> getAllCards() {
        return cardRepository.findAll()
                .stream()
                .map(CardMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CardDTO approveCard(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new RuntimeException("Card not found"));
        if (!"PENDING".equalsIgnoreCase(card.getStatus())) {
            throw new RuntimeException("Card is not in PENDING status");
        }
        card.setStatus("ACTIVE");
        Card updated = cardRepository.save(card);

        return CardMapper.toDTO(updated);
    }

    @Transactional
    public CardDTO rejectCard(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new RuntimeException("Card not found"));
        if (!"PENDING".equalsIgnoreCase(card.getStatus())) {
            throw new RuntimeException("Card is not in PENDING status");
        }
        card.setStatus("REJECTED");
        Card updated = cardRepository.save(card);
        return CardMapper.toDTO(updated);
    }
}
