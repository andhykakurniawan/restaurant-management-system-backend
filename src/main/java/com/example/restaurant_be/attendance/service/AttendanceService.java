package com.example.restaurant_be.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.restaurant_be.attendance.dto.AttendanceRequest;
import com.example.restaurant_be.attendance.dto.AttendanceResponse;
import com.example.restaurant_be.attendance.entity.Attendance;
import com.example.restaurant_be.attendance.entity.Status;
import com.example.restaurant_be.attendance.repository.AttendanceRepository;
import com.example.restaurant_be.shift.entity.Shift;
import com.example.restaurant_be.shift.repository.ShiftRepository;
import com.example.restaurant_be.user.entity.User;
import com.example.restaurant_be.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;

    public AttendanceResponse checkIn(AttendanceRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // String username = authentication.getName();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Shift shift = shiftRepository.findById(request.shiftId())
                .orElseThrow(() -> new IllegalArgumentException("Shift not found"));

        LocalDate today = LocalDate.now();

        if (attendanceRepository.existsByUserIdAndAttendanceDate(user.getId(), today)) {
            throw new IllegalArgumentException("User already checked in today");
        }

        LocalDateTime now = LocalDateTime.now();

        Attendance attendance = new Attendance();
        attendance.setUser(user);
        attendance.setShift(shift);
        attendance.setAttendanceDate(today);
        attendance.setClockIn(now);

        LocalTime checkInTime = now.toLocalTime();
        LocalTime allowedTime = shift.getStartTime().plusMinutes(shift.getGraceMinutes());

        if (checkInTime.isAfter(allowedTime)) {
            attendance.setStatus(Status.LATE);
        } else {
            attendance.setStatus(Status.IN_TIME);
        }

        return toResponse(attendanceRepository.save(attendance));
    }

    public AttendanceResponse checkOut(UUID id) {

        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance not found"));

        if (attendance.getClockOut() != null) {
            throw new IllegalArgumentException("Already checked out");
        }

        LocalDateTime now = LocalDateTime.now();
        attendance.setClockOut(now);

        LocalTime checkOutTime = now.toLocalTime();
        LocalTime shiftEnd = attendance.getShift().getEndTime();

        if (checkOutTime.isBefore(shiftEnd)) {
            attendance.setStatus(Status.EARLY_LEAVE);
        }

        Attendance saved = attendanceRepository.save(attendance);

        return toResponse(saved);
    }

    public AttendanceResponse findById(UUID id) {

        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance not found"));

        return toResponse(attendance);
    }

    public List<AttendanceResponse> findByUser(UUID userId) {

        return attendanceRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AttendanceResponse> findToday() {

        LocalDate today = LocalDate.now();

        return attendanceRepository.findByAttendanceDate(today)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private AttendanceResponse toResponse(Attendance attendance) {

        return new AttendanceResponse(
                attendance.getId(),
                attendance.getUser().getId(),
                attendance.getUser().getUsername(),
                attendance.getShift().getId(),
                attendance.getShift().getShiftName(),
                attendance.getClockIn() != null ? attendance.getClockIn().toString() : null,
                attendance.getClockOut() != null ? attendance.getClockOut().toString() : null,
                attendance.getStatus(),
                attendance.getNotes());
    }
}