package com.cv.profile.controller;

import com.cv.profile.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final CloudinaryService cloudinaryService;

    public FileUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    // 1. API Upload ảnh lên Cloudinary
    // POST /api/v1/upload
    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File không được để trống");
            }

            // Gọi service để upload và nhận về URL ảnh
            String url = cloudinaryService.uploadFile(file);

            // Trả về URL trực tiếp từ Cloudinary
            // VD: https://res.cloudinary.com/demo/image/upload/v123456/uuid_avatar.jpg
            return ResponseEntity.ok(url);

        } catch (IOException ex) {
            return ResponseEntity.badRequest().body("Lỗi upload file: " + ex.getMessage());
        }
    }
}