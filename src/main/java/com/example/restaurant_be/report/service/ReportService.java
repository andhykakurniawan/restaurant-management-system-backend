package com.example.restaurant_be.report.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.restaurant_be.report.dto.DailySalesReportResponse;
import com.example.restaurant_be.report.dto.MonthlySalesReportResponse;
import com.example.restaurant_be.report.dto.YearlySalesReportResponse;
import com.example.restaurant_be.report.projection.SummaryProjection;
import com.example.restaurant_be.report.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public DailySalesReportResponse getDailySales(LocalDate date) {

        SummaryProjection summary = reportRepository.getDailySummary(date);

        Long totalOrders = summary.getTotalOrders();

        BigDecimal grossRevenue = summary.getGrossRevenue();

        Long totalItemsSold = reportRepository.getTotalItemsSold(date);

        List<Object[]> rows = reportRepository.getPaymentBreakdown(date);

        Map<String, BigDecimal> payments = new HashMap<>();

        for (Object[] row : rows) {
            payments.put(
                    row[0].toString(),
                    (BigDecimal) row[1]);
        }

        return new DailySalesReportResponse(
                date,
                totalOrders,
                totalItemsSold,
                grossRevenue,
                payments);
    }

    public MonthlySalesReportResponse getMonthlySales(YearMonth month) {

        LocalDateTime start = month.atDay(1).atStartOfDay();

        LocalDateTime end = month.plusMonths(1)
                .atDay(1)
                .atStartOfDay();

        var summary = reportRepository.getMonthlySummary(start, end);

        Long totalOrders = summary.getTotalOrders();

        BigDecimal grossRevenue = summary.getGrossRevenue();

        Long totalItemsSold = reportRepository.getMonthlyItemsSold(start, end);

        var rows = reportRepository.getMonthlyPaymentBreakdown(start, end);

        Map<String, BigDecimal> payments = new HashMap<>();

        for (var row : rows) {
            payments.put(row.getMethod(), row.getTotal());
        }

        return new MonthlySalesReportResponse(
                month,
                totalOrders,
                totalItemsSold,
                grossRevenue,
                payments);
    }

    public YearlySalesReportResponse getYearlySales(Integer year) {

        LocalDateTime start = LocalDate.of(year, 1, 1)
                .atStartOfDay();

        LocalDateTime end = start.plusYears(1);

        var summary = reportRepository.getYearlySummary(start, end);

        Long totalOrders = summary.getTotalOrders();

        BigDecimal grossRevenue = summary.getGrossRevenue();

        Long totalItemsSold = reportRepository.getYearlyItemsSold(start, end);

        return new YearlySalesReportResponse(
                year,
                totalOrders,
                totalItemsSold,
                grossRevenue);
    }
}