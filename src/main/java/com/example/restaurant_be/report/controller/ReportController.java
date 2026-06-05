package com.example.restaurant_be.report.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_be.report.dto.DailySalesReportResponse;
import com.example.restaurant_be.report.dto.HourlySalesResponse;
import com.example.restaurant_be.report.dto.MonthlySalesReportResponse;
import com.example.restaurant_be.report.dto.RevenueAggregationResponse;
import com.example.restaurant_be.report.dto.SalesByCashierResponse;
import com.example.restaurant_be.report.dto.TopMenuResponse;
import com.example.restaurant_be.report.dto.YearlySalesReportResponse;
import com.example.restaurant_be.report.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily-sales")
    public DailySalesReportResponse getDailySales(
            @RequestParam(required = false) LocalDate date) {

        if (date == null) {
            date = LocalDate.now();
        }

        return reportService.getDailySales(date);
    }

    @GetMapping("/monthly-sales")
    public MonthlySalesReportResponse getMonthlySales(
            @RequestParam YearMonth month) {
        return reportService.getMonthlySales(month);
    }

    @GetMapping("/yearly-sales")
    public YearlySalesReportResponse getYearlySales(
            @RequestParam Integer year) {
        return reportService.getYearlySales(year);
    }

    @GetMapping("/top-menus")
    public List<TopMenuResponse> getTopMenus(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "10") int limit) {
        return reportService.getTopMenus(from, to, limit);
    }

    @GetMapping("/sales-by-cashier")
    public List<SalesByCashierResponse> getSalesByCashier(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.getSalesByCashier(from, to);
    }

    @GetMapping("/hourly-sales")
    public List<HourlySalesResponse> getHourlySales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reportService.getHourlySales(date);
    }

    @GetMapping("/revenue-aggregation")
    public List<RevenueAggregationResponse> getRevenueAggregation(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reportService.getRevenueAggregation(from, to);
    }
}
