package com.example.restaurant_be.shift.service;

import java.util.List;
import java.util.UUID;
import java.time.LocalTime;
import org.springframework.stereotype.Service;
import com.example.restaurant_be.shift.dto.ShiftRequest;
import com.example.restaurant_be.shift.dto.ShiftResponse;
import com.example.restaurant_be.shift.repository.ShiftRepository;
import com.example.restaurant_be.shift.entity.Shift;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShiftService {
    private final ShiftRepository shiftRepository;

    public List<ShiftResponse> findAll() {
        return shiftRepository.findAllIncludingInactive()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ShiftResponse toResponse(Shift shift) {
        return new ShiftResponse(
                shift.getId(),
                shift.getShiftName(),
                shift.getStartTime().toString(),
                shift.getEndTime().toString(),
                shift.getGraceMinutes(),
                shift.getIsActive()
        );
    }

    public ShiftResponse create(ShiftRequest request) {
        Shift shift = new Shift();
        shift.setShiftName(request.shiftName());
        shift.setStartTime(LocalTime.parse(request.startTime()));
        shift.setEndTime(LocalTime.parse(request.endTime()));
        shift.setGraceMinutes(request.graceMinutes());

        Shift saved = shiftRepository.save(shift);
        return toResponse(saved);
    }

    public ShiftResponse findById(UUID id) {
        Shift shift = shiftRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new RuntimeException("Shift not found"));
        return toResponse(shift);
    }

    public void delete(UUID id) {
        Shift shift = shiftRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new RuntimeException("Shift not found"));
        shiftRepository.delete(shift);
    }

    public ShiftResponse restore(UUID id) {
        Shift shift = shiftRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new RuntimeException("Shift not found"));
        shift.setIsActive(true);
        Shift saved = shiftRepository.save(shift);
        return toResponse(saved);
    }
}
