package com.cv.profile.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // Tự động tạo public_id (tên file trên cloud) độc nhất để tránh trùng
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // Upload file lên Cloudinary
        // "public_id" quy định tên file trên Cloudinary
        // "resource_type": "auto" để tự động nhận diện ảnh/video/file khác
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("public_id", fileName));

        // Trả về đường dẫn URL an toàn (https)
        return uploadResult.get("secure_url").toString();
    }
}