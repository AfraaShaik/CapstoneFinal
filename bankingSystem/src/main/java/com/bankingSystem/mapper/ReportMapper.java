package com.bankingSystem.mapper;

import com.bankingSystem.DTO.ReportDTO;
import com.bankingSystem.entity.Report;

public class ReportMapper {
    public static ReportDTO toDTO(Report report) {
        ReportDTO dto = new ReportDTO();
        dto.setReportId(report.getReportId());
        dto.setType(report.getType());
        dto.setGeneratedDate(report.getGeneratedDate());
        dto.setData(report.getData());
        dto.setCreatedBy(report.getCreatedBy());
        return dto;
    }
}
