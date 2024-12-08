package com.bankingSystem.controller;

import com.bankingSystem.DTO.CardDTO;
import com.bankingSystem.DTO.CardRequest;
import com.bankingSystem.entity.User;
import com.bankingSystem.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardDTO> applyForCard(@RequestBody CardRequest request, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        if (!"CUSTOMER".equalsIgnoreCase(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }
        CardDTO card = cardService.applyForCard(currentUser.getUserId(), request);
        return ResponseEntity.ok(card);
    }

    @GetMapping("/me")
    public ResponseEntity<List<CardDTO>> getMyCards(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        if (!"CUSTOMER".equalsIgnoreCase(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }
        List<CardDTO> cards = cardService.getCardsForUser(currentUser.getUserId());
        return ResponseEntity.ok(cards);
    }

    @PutMapping("/{cardId}/block")
    public ResponseEntity<CardDTO> blockCard(@PathVariable Long cardId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        if (!"CUSTOMER".equalsIgnoreCase(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }
        CardDTO updated = cardService.blockCard(currentUser.getUserId(), cardId);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{cardId}/lost")
    public ResponseEntity<CardDTO> reportLost(@PathVariable Long cardId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        if (!"CUSTOMER".equalsIgnoreCase(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }
        CardDTO updated = cardService.reportLostOrStolen(currentUser.getUserId(), cardId);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{cardId}/replace")
    public ResponseEntity<CardDTO> replaceCard(@PathVariable Long cardId, @RequestBody CardRequest newCard, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        if (!"CUSTOMER".equalsIgnoreCase(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }
        CardDTO replaced = cardService.replaceCard(currentUser.getUserId(), cardId, newCard);
        return ResponseEntity.ok(replaced);
    }

    // ADMIN endpoints
    @GetMapping
    public ResponseEntity<List<CardDTO>> getAllCards(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }

        List<CardDTO> allCards = cardService.getAllCards();
        return ResponseEntity.ok(allCards);
    }

    @PutMapping("/{cardId}/approve")
    public ResponseEntity<CardDTO> approveCard(@PathVariable Long cardId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }

        CardDTO approved = cardService.approveCard(cardId);
        return ResponseEntity.ok(approved);
    }

    @PutMapping("/{cardId}/reject")
    public ResponseEntity<CardDTO> rejectCard(@PathVariable Long cardId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        if (!"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            return ResponseEntity.status(403).build();
        }

        CardDTO rejected = cardService.rejectCard(cardId);
        return ResponseEntity.ok(rejected);
    }
}
