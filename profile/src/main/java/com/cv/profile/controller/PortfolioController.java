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

    @GetMapping("/view/{rid}/{lid}/{did}/{pid}")
    public ResponseEntity<?> getFullHierarchy(
            @PathVariable Long rid,
            @PathVariable Long lid,
            @PathVariable Long did,
            @PathVariable Long pid) {
        try {
            PortfolioDTO result = portfolioService.getPortfolioWithFullValidation(rid, lid, did, pid);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi truy xuất: " + e.getMessage());
        }
    }

    @GetMapping("/view/code/{regionCode}/{localCode}/{deptCode}/{pid}")
    public ResponseEntity<?> getPortfolioByHierarchyCodes(
            @PathVariable String regionCode,
            @PathVariable String localCode,
            @PathVariable String deptCode,
            @PathVariable Long pid) {
        try {
            // Gọi Service xử lý logic check Code
            PortfolioDTO result = portfolioService.getPortfolioByHierarchyCodes(regionCode, localCode, deptCode, pid);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}