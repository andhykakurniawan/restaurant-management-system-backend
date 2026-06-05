package com.example.restaurant_be.report.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.restaurant_be.payment.entity.Payment;
import com.example.restaurant_be.report.dto.DailySalesReportResponse;
import com.example.restaurant_be.report.dto.HourlySalesResponse;
import com.example.restaurant_be.report.dto.MonthlySalesReportResponse;
import com.example.restaurant_be.report.dto.RevenueAggregationResponse;
import com.example.restaurant_be.report.dto.SalesByCashierResponse;
import com.example.restaurant_be.report.dto.TopMenuResponse;
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

    public List<TopMenuResponse> getTopMenus(LocalDate from, LocalDate to, int limit) {
        DateRange range = toDateRange(from, to);

        return reportRepository.getTopMenus(
                range.start(),
                range.end(),
                PageRequest.of(0, Math.max(1, limit)))
                .stream()
                .map(row -> new TopMenuResponse(
                        (java.util.UUID) row[0],
                        (String) row[1],
                        ((Number) row[2]).longValue(),
                        (BigDecimal) row[3]))
                .toList();
    }

    public List<SalesByCashierResponse> getSalesByCashier(LocalDate from, LocalDate to) {
        DateRange range = toDateRange(from, to);

        return reportRepository.getSalesByCashier(range.start(), range.end())
                .stream()
                .map(row -> new SalesByCashierResponse(
                        (java.util.UUID) row[0],
                        row[1] != null ? (String) row[1] : "Unassigned",
                        ((Number) row[2]).longValue(),
                        (BigDecimal) row[3]))
                .toList();
    }

    public List<HourlySalesResponse> getHourlySales(LocalDate date) {
        LocalDate targetDate = date != null ? date : LocalDate.now();

        List<Payment> payments = reportRepository.findSuccessfulPaymentsBetween(
                targetDate.atStartOfDay(),
                targetDate.plusDays(1).atStartOfDay());

        Map<Integer, HourlyBucket> buckets = new TreeMap<>();

        for (Payment payment : payments) {
            int hour = payment.getPaidAt().getHour();
            HourlyBucket bucket = buckets.computeIfAbsent(hour, ignored -> new HourlyBucket());
            bucket.totalPayments++;
            bucket.totalSales = bucket.totalSales.add(payment.getAmount());
        }

        return buckets.entrySet()
                .stream()
                .map(entry -> new HourlySalesResponse(
                        entry.getKey(),
                        entry.getValue().totalPayments,
                        entry.getValue().totalSales))
                .toList();
    }

    public List<RevenueAggregationResponse> getRevenueAggregation(LocalDate from, LocalDate to) {
        DateRange range = toDateRange(from, to);
        List<Payment> payments = reportRepository.findSuccessfulPaymentsBetween(range.start(), range.end());

        Map<LocalDate, HourlyBucket> buckets = new TreeMap<>();

        for (Payment payment : payments) {
            LocalDate period = payment.getPaidAt().toLocalDate();
            HourlyBucket bucket = buckets.computeIfAbsent(period, ignored -> new HourlyBucket());
            bucket.totalPayments++;
            bucket.totalSales = bucket.totalSales.add(payment.getAmount());
        }

        return buckets.entrySet()
                .stream()
                .map(entry -> new RevenueAggregationResponse(
                        entry.getKey(),
                        entry.getValue().totalPayments,
                        entry.getValue().totalSales))
                .toList();
    }

    private DateRange toDateRange(LocalDate from, LocalDate to) {
        LocalDate startDate = from != null ? from : LocalDate.now();
        LocalDate endDate = to != null ? to : startDate;

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("to date must be equal to or after from date");
        }

        return new DateRange(
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay());
    }

    private record DateRange(LocalDateTime start, LocalDateTime end) {
    }

    private static class HourlyBucket {
        private long totalPayments;
        private BigDecimal totalSales = BigDecimal.ZERO;
    }
}
