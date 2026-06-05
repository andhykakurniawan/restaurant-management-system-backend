package com.example.restaurant_be.table.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.restaurant_be.common.response.ApiResponse;
import com.example.restaurant_be.table.dto.TableRequest;
import com.example.restaurant_be.table.dto.TableResponse;
import com.example.restaurant_be.table.service.TableService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/table-restaurants")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TableResponse>>> findAll() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Tables retrieved successfully",
                        tableService.findAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TableResponse>> create(
            @RequestBody @Valid TableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Table created successfully",
                        tableService.create(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TableResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Table retrieved successfully",
                tableService.findById(id)));
    }

    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TableResponse>> update(
            @PathVariable UUID id,
            @RequestBody @Valid TableRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Table updated successfully",
                        tableService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteById(@PathVariable UUID id) {
        tableService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Table deleted successfully", null));
    }

    @PatchMapping("/{id}/restore")
    // @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<TableResponse>> restore(@PathVariable UUID id) {

        TableResponse response = tableService.restore(id);

        return ResponseEntity.ok(
                ApiResponse.success("Table restored successfully", response));
    }

}
