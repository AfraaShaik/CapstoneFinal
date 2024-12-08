package com.bankingSystem.controller;

import com.bankingSystem.DTO.SupportTicketDTO;
import com.bankingSystem.DTO.SupportTicketRequest;
import com.bankingSystem.entity.User;
import com.bankingSystem.service.SupportTicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support/tickets")

public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    public SupportTicketController(SupportTicketService supportTicketService) {
        this.supportTicketService = supportTicketService;
    }

    @PostMapping
    public ResponseEntity<SupportTicketDTO> createTicket(@RequestBody SupportTicketRequest request, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        if (!"CUSTOMER".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }

        SupportTicketDTO ticket = supportTicketService.createTicket(currentUser.getUserId(), request);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<SupportTicketDTO> getTicket(@PathVariable Long ticketId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        SupportTicketDTO ticket = supportTicketService.getTicketById(ticketId);
        // If CUSTOMER, must own the ticket
        if ("CUSTOMER".equalsIgnoreCase(role) && !ticket.getUserId().equals(currentUser.getUserId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SupportTicketDTO>> getTicketsForUser(@PathVariable Long userId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        // CUSTOMER can only view their own tickets
        if ("CUSTOMER".equalsIgnoreCase(role) && !userId.equals(currentUser.getUserId())) {
            return ResponseEntity.status(403).build();
        }

        List<SupportTicketDTO> tickets = supportTicketService.getTicketsForUser(userId);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping
    public ResponseEntity<List<SupportTicketDTO>> getAllTickets(Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }

        List<SupportTicketDTO> tickets = supportTicketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{ticketId}/resolve")
    public ResponseEntity<SupportTicketDTO> resolveTicket(@PathVariable Long ticketId, Authentication auth) {
        User currentUser = (User) auth.getPrincipal();
        String role = currentUser.getRole();

        if (!"ADMIN".equalsIgnoreCase(role)) {
            return ResponseEntity.status(403).build();
        }

        SupportTicketDTO resolved = supportTicketService.resolveTicket(ticketId);
        return ResponseEntity.ok(resolved);
    }
    @GetMapping("/faq")
    public ResponseEntity<List<String>> getFaq() {
        List<String> faqs = List.of(
                "Q: How to check my account balance? A: Go to 'My Accounts' section.",
                "Q: How to request a loan? A: Use the 'Loans' tab to apply."
        );
        return ResponseEntity.ok(faqs);
    }

}
