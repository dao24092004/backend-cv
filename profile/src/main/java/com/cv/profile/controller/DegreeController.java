package com.cv.profile.controller;

import com.cv.profile.dto.request.DegreeRequest;
import com.cv.profile.service.DegreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/degrees")
@RequiredArgsConstructor
public class DegreeController {

    private final DegreeService degreeService;

    // --- GIAI ĐOẠN 1: CẤP NHÁP (DRAFT) ---
    @PostMapping("/draft/single")
    public ResponseEntity<?> createDraftSingle(@RequestBody DegreeRequest request) {
        try {
            return ResponseEntity.ok(degreeService.createDraftSingle(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/draft/batch")
    public ResponseEntity<?> createDraftBatch(@RequestBody List<DegreeRequest> requests) {
        return ResponseEntity.ok(degreeService.createDraftBatch(requests));
    }

    // --- GIAI ĐOẠN 2: KÝ & PHÁT HÀNH (SIGN) ---
    @GetMapping("/pending") // Lấy danh sách chờ ký cho Admin xem
    public ResponseEntity<?> getPendingList() {
        return ResponseEntity.ok(degreeService.getPendingDegrees());
    }

    @PostMapping("/sign/single/{id}")
    public ResponseEntity<?> signDegree(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(degreeService.signSingle(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/sign/batch")
    public ResponseEntity<?> signBatch(@RequestBody List<Long> ids) {
        return ResponseEntity.ok(degreeService.signBatch(ids));
    }

    // --- CÁC API KHÁC ---
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getByProfile(@PathVariable Long id) {
        return ResponseEntity.ok(degreeService.getDegreesByProfile(id));
    }

    @PostMapping("/verify-file")
    public ResponseEntity<?> verifyFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(degreeService.verifyFile(file));
    }
}