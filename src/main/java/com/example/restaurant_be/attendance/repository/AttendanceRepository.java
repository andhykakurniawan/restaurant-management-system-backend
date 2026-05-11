package com.example.restaurant_be.attendance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.restaurant_be.attendance.entity.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance, UUID> {
    @Query(value = "select * from attendances where id = :id", nativeQuery = true)
    Optional<Attendance> findByIdIncludingInactive(UUID id);

    @Query(value = "select * from attendances where user_id = :userId", nativeQuery = true)
    List<Attendance> findByUserIdIncludingInactive(UUID userId);

    @Query(value = "select * from attendances where shift_id = :shiftId", nativeQuery = true)
    List<Attendance> findByShiftIdIncludingInactive(UUID shiftId);

    @Query(value = "select * from attendances", nativeQuery = true)
    List<Attendance> findAllIncludingInactive();

    boolean existsByUserIdAndAttendanceDate(UUID userId, LocalDate attendanceDate);
    List<Attendance> findByUserId(UUID userId);
    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);
}
