package com.bankingSystem.service;

import com.bankingSystem.DTO.SupportTicketDTO;
import com.bankingSystem.DTO.SupportTicketRequest;
import com.bankingSystem.entity.SupportTicket;
import com.bankingSystem.mapper.SupportTicketMapper;
import com.bankingSystem.repository.SupportTicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketService(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    @Transactional
    public SupportTicketDTO createTicket(Long userId, SupportTicketRequest request) {
        SupportTicket ticket = new SupportTicket();
        ticket.setUserId(userId);
        ticket.setCategory(request.getCategory());
        ticket.setMessage(request.getMessage());
        // Status and createdDate set by @PrePersist (OPEN, current time)

        SupportTicket saved = supportTicketRepository.save(ticket);
        return SupportTicketMapper.toDTO(saved);
    }

    public SupportTicketDTO getTicketById(Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return SupportTicketMapper.toDTO(ticket);
    }

    public List<SupportTicketDTO> getTicketsForUser(Long userId) {
        return supportTicketRepository.findByUserId(userId)
                .stream()
                .map(SupportTicketMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<SupportTicketDTO> getAllTickets() {
        return supportTicketRepository.findAll()
                .stream()
                .map(SupportTicketMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SupportTicketDTO resolveTicket(Long ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setStatus("RESOLVED");
        ticket.setResolvedDate(LocalDateTime.now());
        SupportTicket updated = supportTicketRepository.save(ticket);
        return SupportTicketMapper.toDTO(updated);
    }
}
