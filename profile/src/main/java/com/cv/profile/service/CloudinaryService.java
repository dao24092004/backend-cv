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

    // Upload file từ FE gửi lên (Giữ nguyên)
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("public_id", fileName));
        return uploadResult.get("secure_url").toString();
    }

    public String uploadByteFile(byte[] fileBytes, String fileNamePrefix) throws IOException {

        String fileName = fileNamePrefix + "_" + UUID.randomUUID().toString() + ".pdf";

        Map uploadResult = cloudinary.uploader().upload(fileBytes,
                ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "auto"));

        return uploadResult.get("secure_url").toString();
    }
}