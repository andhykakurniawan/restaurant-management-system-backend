package com.example.restaurant_be.report.controller;

import java.time.LocalDate;
import java.time.YearMonth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_be.report.dto.DailySalesReportResponse;
import com.example.restaurant_be.report.dto.MonthlySalesReportResponse;
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
}