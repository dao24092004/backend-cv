package com.cv.profile.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface CvImageExtractor {
    public String extractAvatar(MultipartFile file);

}