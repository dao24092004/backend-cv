package com.cv.profile.service;

import com.cv.profile.dto.request.DegreeRequest;
import com.cv.profile.dto.response.DegreeIssueResponse;
import com.cv.profile.model.Degree;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface DegreeService {
    // Phase 1: Tạo Nháp (Draft)
    Degree createDraftSingle(DegreeRequest request);

    List<Degree> createDraftBatch(List<DegreeRequest> requests);

    // Phase 2: Ký Duyệt (Sign)
    DegreeIssueResponse signSingle(Long degreeId);

    Map<String, Object> signBatch(List<Long> degreeIds);

    // Các hàm phụ trợ
    List<Degree> getDegreesByProfile(Long id);

    List<Degree> getPendingDegrees(); // Lấy danh sách chờ ký

    Degree getDegreeBySerial(String sn);

    DegreeIssueResponse verifyFile(MultipartFile file);

    Map<String, Long> getStats();
}