package com.example.restaurant_be.report.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.restaurant_be.payment.entity.Payment;
import com.example.restaurant_be.report.projection.SummaryProjection;
import com.example.restaurant_be.report.projection.PaymentMethodProjection;

public interface ReportRepository extends JpaRepository<Payment, UUID> {

    @Query("""
                SELECT COUNT(p) AS totalOrders,
                       COALESCE(SUM(p.amount),0) AS grossRevenue
                FROM Payment p
                WHERE FUNCTION('DATE', p.paidAt) = :date
                AND p.status = 'SUCCESS'
            """)
    SummaryProjection getDailySummary(@Param("date") LocalDate date);

    @Query("""
                SELECT COALESCE(SUM(oi.quantity), 0)
                FROM OrderItem oi
                JOIN Payment p ON oi.order = p.order
                WHERE FUNCTION('DATE', p.paidAt) = :date
                AND p.status = 'SUCCESS'
            """)
    Long getTotalItemsSold(@Param("date") LocalDate date);

    @Query("""
                SELECT p.method, COALESCE(SUM(p.amount), 0)
                FROM Payment p
                WHERE FUNCTION('DATE', p.paidAt) = :date
                AND p.status = 'SUCCESS'
                GROUP BY p.method
            """)
    List<Object[]> getPaymentBreakdown(@Param("date") LocalDate date);

    @Query("""
                SELECT COUNT(p) AS totalOrders,
                       COALESCE(SUM(p.amount),0) AS grossRevenue
                FROM Payment p
                WHERE p.paidAt >= :start
                AND p.paidAt < :end
                AND p.status = 'SUCCESS'
            """)
    SummaryProjection getMonthlySummary(
            LocalDateTime start,
            LocalDateTime end);

    @Query("""
                SELECT COALESCE(SUM(oi.quantity),0)
                FROM OrderItem oi
                JOIN Payment p ON oi.order = p.order
                WHERE p.paidAt >= :start
                AND p.paidAt < :end
                AND p.status = 'SUCCESS'
            """)
    Long getMonthlyItemsSold(
            LocalDateTime start,
            LocalDateTime end);

    @Query("""
                SELECT p.method AS method,
                       COALESCE(SUM(p.amount),0) AS total
                FROM Payment p
                WHERE p.paidAt >= :start
                AND p.paidAt < :end
                AND p.status = 'SUCCESS'
                GROUP BY p.method
            """)
    List<PaymentMethodProjection> getMonthlyPaymentBreakdown(
            LocalDateTime start,
            LocalDateTime end);

    @Query("""
                SELECT COUNT(p) AS totalOrders,
                       COALESCE(SUM(p.amount),0) AS grossRevenue
                FROM Payment p
                WHERE p.paidAt >= :start
                AND p.paidAt < :end
                AND p.status = 'SUCCESS'
            """)
    SummaryProjection getYearlySummary(
            LocalDateTime start,
            LocalDateTime end);

    @Query("""
                SELECT COALESCE(SUM(oi.quantity),0)
                FROM OrderItem oi
                JOIN Payment p ON oi.order = p.order
                WHERE p.paidAt >= :start
                AND p.paidAt < :end
                AND p.status = 'SUCCESS'
            """)
    Long getYearlyItemsSold(
            LocalDateTime start,
            LocalDateTime end);
}