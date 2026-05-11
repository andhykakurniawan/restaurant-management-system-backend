package com.example.restaurant_be.table.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.restaurant_be.table.dto.TableRequest;
import com.example.restaurant_be.table.dto.TableResponse;
import com.example.restaurant_be.table.entity.TableRestaurant;
import com.example.restaurant_be.table.entity.TableStatus;
import com.example.restaurant_be.table.repository.TableRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TableService {
    private final TableRepository tableRepository;

    public List<TableResponse> findAll() {
        return tableRepository.findAllIncludingInactive()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TableResponse toResponse(TableRestaurant tableRestaurant) {
        return new TableResponse(
                tableRestaurant.getId(),
                tableRestaurant.getTableNumber(),
                tableRestaurant.getCapacity(),
                tableRestaurant.getAllocationType(),
                tableRestaurant.getStatus(),
                tableRestaurant.getArea(),
                tableRestaurant.getIsActive());
    }

    public TableResponse create(TableRequest request) {
        if (tableRepository.existsByTableNumber(request.tableNumber())) {
            throw new IllegalArgumentException("Table number already exists");
        }

        TableRestaurant table = new TableRestaurant();
        table.setTableNumber(request.tableNumber());
        table.setCapacity(request.capacity());
        table.setAllocationType(request.allocationType());
        table.setStatus(TableStatus.AVAILABLE);
        table.setArea(request.area());

        TableRestaurant savedTable = tableRepository.save(table);

        return toResponse(savedTable);
    }

    public TableResponse update(UUID id, TableRequest request) {
        TableRestaurant table = tableRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new IllegalArgumentException("Table not found"));

        if (!table.getTableNumber().equals(request.tableNumber()) &&
                tableRepository.existsByTableNumber(request.tableNumber())) {
            throw new IllegalArgumentException("Table number already exists");
        }

        table.setTableNumber(request.tableNumber());
        table.setCapacity(request.capacity());
        table.setAllocationType(request.allocationType());
        table.setStatus(request.status());
        table.setArea(request.area());

        TableRestaurant updatedTable = tableRepository.save(table);

        return toResponse(updatedTable);
    }

    public TableResponse findById(UUID id) {
        TableRestaurant table = tableRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new IllegalArgumentException("Table not found"));

        return toResponse(table);
    }

    public void deleteById(UUID id) {
        TableRestaurant table = tableRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new IllegalArgumentException("Table not found"));

        tableRepository.delete(table);
    }

    public TableResponse restore(UUID id) {
        TableRestaurant table = tableRepository.findByIdIncludingInactive(id)
                .orElseThrow(() -> new IllegalArgumentException("Table not found"));

        if (Boolean.TRUE.equals(table.getIsActive())) {
            throw new IllegalArgumentException("Table already active");
        }

        table.setIsActive(true);
        TableRestaurant restored = tableRepository.save(table);
        return toResponse(restored);
    }
}
