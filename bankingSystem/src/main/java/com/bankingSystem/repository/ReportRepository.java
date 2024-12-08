package com.bankingSystem.repository;

import com.bankingSystem.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByType(String type);
}
