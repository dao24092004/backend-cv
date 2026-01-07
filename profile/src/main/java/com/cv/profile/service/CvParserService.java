package com.cv.profile.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import com.cv.profile.dto.ai.CVExtractionResult;

public interface CvParserService {

    public CVExtractionResult parseResume(MultipartFile file) throws IOException;
}
