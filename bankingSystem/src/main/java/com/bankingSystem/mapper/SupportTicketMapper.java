package com.bankingSystem.mapper;

import com.bankingSystem.DTO.SupportTicketDTO;
import com.bankingSystem.entity.SupportTicket;

public class SupportTicketMapper {
    public static SupportTicketDTO toDTO(SupportTicket ticket) {
        SupportTicketDTO dto = new SupportTicketDTO();
        dto.setTicketId(ticket.getTicketId());
        dto.setUserId(ticket.getUserId());
        dto.setCategory(ticket.getCategory());
        dto.setMessage(ticket.getMessage());
        dto.setStatus(ticket.getStatus());
        dto.setCreatedDate(ticket.getCreatedDate());
        dto.setResolvedDate(ticket.getResolvedDate());
        return dto;
    }
}
