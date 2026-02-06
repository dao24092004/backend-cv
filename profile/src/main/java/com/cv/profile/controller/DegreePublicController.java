package com.cv.profile.controller;

import com.cv.profile.model.Degree;
import com.cv.profile.model.DegreeStatus;
import com.cv.profile.service.DegreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/degrees") // Endpoint Công khai
@RequiredArgsConstructor
public class DegreePublicController {

    private final DegreeService degreeService;

    // API này dùng cho:
    // 1. Frontend gọi khi người dùng nhập mã tìm kiếm
    // 2. Frontend gọi khi người dùng quét QR (lấy serial từ URL)
    @GetMapping("/{serialNumber}")
    public ResponseEntity<?> getPublicDegree(@PathVariable String serialNumber) {
        Degree degree = degreeService.getDegreeBySerial(serialNumber);

        if (degree == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy văn bằng.");
        }

        // BẢO MẬT: Chỉ cho xem bằng đã cấp (ISSUED)
        if (degree.getStatus() != DegreeStatus.ISSUED) {
            return ResponseEntity.badRequest().body("Văn bằng chưa hợp lệ hoặc đã bị thu hồi.");
        }

        return ResponseEntity.ok(degree);
    }
}