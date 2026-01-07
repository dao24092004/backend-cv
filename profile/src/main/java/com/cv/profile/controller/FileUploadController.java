package com.cv.profile.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {

    // Đường dẫn thư mục lưu ảnh (Nằm ngay trong thư mục project)
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public FileUploadController() {
        try {
            // Tạo thư mục uploads nếu chưa có
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo thư mục upload.", ex);
        }
    }

    // 1. API Upload ảnh
    // POST /api/v1/upload
    // Body: form-data (key: "file", value: [chọn file])
    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Tạo tên file ngẫu nhiên để tránh trùng (VD: uuid_avatar.png)
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String randomFileName = UUID.randomUUID().toString() + "_" + fileName;

            // Lưu file vào ổ cứng
            Path targetLocation = this.fileStorageLocation.resolve(randomFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Trả về URL để Frontend truy cập
            // VD: http://localhost:8080/uploads/uuid_avatar.png
            String fileUrl = "http://localhost:8080/uploads/" + randomFileName;

            return ResponseEntity.ok(fileUrl);
        } catch (IOException ex) {
            return ResponseEntity.badRequest().body("Lỗi upload file: " + ex.getMessage());
        }
    }

    // 2. API Xem ảnh (Nếu cấu hình Static Resource bên dưới gặp lỗi)
    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // Hoặc đoán ContentType động
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}