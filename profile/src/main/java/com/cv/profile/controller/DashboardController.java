package com.cv.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.cv.profile.service.DegreeService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DashboardController {
    private final DegreeService degreeService;
    private final RestTemplate restTemplate;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(degreeService.getStats());
    }

    @GetMapping("/blockchain/info")
    public ResponseEntity<?> getInfo() {
        String url = "http://localhost:3001/api/degree/info";
        try {
            return ResponseEntity.ok(restTemplate.getForObject(url, Object.class));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Node.js offline");
        }
    }
}