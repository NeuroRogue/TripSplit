package com.tripsplit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
@RestController
public class TripSplitApplication {

    public static void main(String[] args) {
        SpringApplication.run(TripSplitApplication.class, args);
    }

    @GetMapping("/api/health")
    public Map<String, Object> healthCheck() {
        return Map.of(
            "status", "UP",
            "application", "TripSplit - Smart Group Trip Expense Management System",
            "version", "1.0.0"
        );
    }
}

