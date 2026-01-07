package com.cv.profile.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cv.profile.dto.response.PortfolioDTO;
import com.cv.profile.service.PortfolioService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Cho phép Frontend gọi API
public class PortfolioController {

    private final PortfolioService portfolioService;

    // 3. Lấy hồ sơ mặc định (Public Profile)
    // URL: GET http://localhost:8080/api/v1/portfolio
    @GetMapping("/portfolio")
    public ResponseEntity<?> getPublicProfile() {
        try {
            return ResponseEntity.ok(portfolioService.getPublicPortfolio());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}