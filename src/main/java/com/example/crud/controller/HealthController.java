package com.example.crud.controller;

import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<HealthStatus> health() {
        HealthStatus status = new HealthStatus();
        status.setStatus("UP");
        status.setTimestamp(LocalDateTime.now());
        status.setService("crud-springboot");
        return ResponseEntity.ok(status);
    }
    
    @Data
    public static class HealthStatus {
        private String status;
        private LocalDateTime timestamp;
        private String service;
    }
}