package com.cv.profile.service.impls;

import com.cv.profile.dto.request.DegreeRequest;
import com.cv.profile.dto.response.DegreeIssueResponse;
import com.cv.profile.model.*;
import com.cv.profile.repository.DegreeRepository;
import com.cv.profile.repository.ProfileRepository;
import com.cv.profile.service.DegreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Year;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DegreeServiceImpl implements DegreeService {

    private final DegreeRepository degreeRepository;
    private final ProfileRepository profileRepository;
    private final RestTemplate restTemplate;

    @Value("${blockchain.service.url}")
    private String blockchainServiceUrl;

    @Value("${app.frontend.url}")
    private String frontendBaseUrl;

    // --- HELPER: TỰ SINH MÃ ---
    private String generateSerialNumber() {
        return "SN" + Year.now().getValue() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String generateStudentId() {
        String yearPrefix = String.valueOf(Year.now().getValue()).substring(2);
        int randomNum = 10000 + new Random().nextInt(90000);
        return "B" + yearPrefix + randomNum;
    }

    private boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }

    // Helper: Lấy Education mới nhất từ Profile
    private Education getLatestEducation(Profile profile) {
        if (profile.getEducation() == null || profile.getEducation().isEmpty())
            return null;
        // Giả sử Education có field 'endDate' (LocalDate) hoặc 'toYear' (Integer)
        // Nếu không có, lấy phần tử đầu tiên
        return profile.getEducation().get(0);
    }

    // =================================================================
    // PHASE 1: TẠO DRAFT (CHƯA KÝ)
    // =================================================================
    @Override
    @Transactional
    public Degree createDraftSingle(DegreeRequest request) {
        Profile profile = profileRepository.findById(request.getProfileId())
                .orElseThrow(() -> new RuntimeException("Profile not found ID: " + request.getProfileId()));

        Degree degree = new Degree();
        degree.setProfile(profile);

        // 1. Merge Tên: Request > Profile > Error
        if (hasText(request.getStudentName()))
            degree.setStudentName(request.getStudentName());
        else if (hasText(profile.getFullName()))
            degree.setStudentName(profile.getFullName());
        else
            throw new RuntimeException("Thiếu tên sinh viên");

        // 2. Merge DOB: Request > Error (Vì Profile chưa có field dob)
        if (hasText(request.getDob()))
            degree.setDob(request.getDob());
        else
            degree.setDob(""); // Để trống chờ Admin nhập sau, hoặc báo lỗi tùy bạn

        // 3. Lấy thông tin học vấn từ Profile nếu Request thiếu
        Education edu = getLatestEducation(profile);

        // Ngành
        if (hasText(request.getMajor()))
            degree.setMajor(request.getMajor());

        else
            degree.setMajor("Công nghệ thông tin"); // Mặc định

        // Tên bằng
        if (hasText(request.getDegreeName()))
            degree.setDegreeName(request.getDegreeName());
        else if (edu != null && hasText(edu.getDegreeVi()))
            degree.setDegreeName(edu.getDegreeVi());
        else
            degree.setDegreeName("KỸ SƯ CÔNG NGHỆ THÔNG TIN");

        // Năm tốt nghiệp
        if (hasText(request.getGraduationYear())) {
            degree.setGraduationYear(LocalDate.of(Integer.parseInt(request.getGraduationYear()), 1, 1));
        } else {
            degree.setGraduationYear(LocalDate.now());
        }

        // Xếp loại
        degree.setClassification(hasText(request.getClassification()) ? request.getClassification() : "Khá");

        // 4. Sinh mã tự động
        degree.setSerialNumber(hasText(request.getSerialNumber()) ? request.getSerialNumber() : generateSerialNumber());
        degree.setStudentId(hasText(request.getStudentId()) ? request.getStudentId() : generateStudentId());

        // 5. Trạng thái PENDING
        degree.setStatus(DegreeStatus.PENDING);

        return degreeRepository.save(degree);
    }

    @Override
    @Transactional
    public List<Degree> createDraftBatch(List<DegreeRequest> requests) {
        List<Degree> list = new ArrayList<>();
        for (DegreeRequest req : requests) {
            list.add(createDraftSingle(req));
        }
        return list;
    }

    // =================================================================
    // PHASE 2: KÝ DUYỆT (GỌI NODE.JS & BLOCKCHAIN)
    // =================================================================
    @Override
    public DegreeIssueResponse signSingle(Long degreeId) {
        Degree degree = degreeRepository.findById(degreeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy văn bằng ID: " + degreeId));

        if (degree.getStatus() == DegreeStatus.ISSUED) {
            throw new RuntimeException("Văn bằng này đã được ký rồi!");
        }

        // 1. Tạo request gửi sang Node.js từ dữ liệu DB
        DegreeRequest nodeReq = new DegreeRequest();
        nodeReq.setStudentName(degree.getStudentName());
        nodeReq.setDob(degree.getDob());
        nodeReq.setSerialNumber(degree.getSerialNumber());
        nodeReq.setStudentId(degree.getStudentId());
        nodeReq.setDegreeName(degree.getDegreeName());
        nodeReq.setMajor(degree.getMajor());
        nodeReq.setClassification(degree.getClassification());
        nodeReq.setGraduationYear(String.valueOf(degree.getGraduationYear().getYear()));
        String qrLink = frontendBaseUrl + "/verify/" + degree.getSerialNumber();
        nodeReq.setVerificationLink(qrLink);
        // 2. Gọi Node.js (Generate PDF & Upload)
        String genUrl = blockchainServiceUrl + "/generate-pdf";
        DegreeIssueResponse genRes = restTemplate.postForObject(genUrl, nodeReq, DegreeIssueResponse.class);

        if (genRes == null || !genRes.isSuccess()) {
            throw new RuntimeException("Node.js Error: " + (genRes != null ? genRes.getError() : "Unknown"));
        }

        // 3. Gọi Blockchain (Commit)
        String commitUrl = blockchainServiceUrl + "/commit";
        Map<String, String> commitBody = Map.of("documentHash", genRes.getDocumentHash());
        DegreeIssueResponse commitRes = restTemplate.postForObject(commitUrl, commitBody, DegreeIssueResponse.class);

        if (commitRes == null || !commitRes.isSuccess()) {
            throw new RuntimeException("Blockchain Error");
        }

        // 4. Cập nhật DB -> ISSUED
        degree.setPdfUrl(genRes.getPdfUrl());
        degree.setQrCodeUrl(genRes.getQrCodeUrl());
        degree.setDocumentHash(genRes.getDocumentHash());
        degree.setTransactionHash(commitRes.getTransactionHash());
        degree.setStatus(DegreeStatus.ISSUED);

        degreeRepository.save(degree);

        genRes.setTransactionHash(commitRes.getTransactionHash());
        return genRes;
    }

    @Override
    public Map<String, Object> signBatch(List<Long> degreeIds) {
        List<DegreeIssueResponse> success = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Long id : degreeIds) {
            try {
                success.add(signSingle(id));
                Thread.sleep(1000); // Delay nhẹ tránh spam
            } catch (Exception e) {
                errors.add("ID " + id + ": " + e.getMessage());
            }
        }
        Map<String, Object> res = new HashMap<>();
        res.put("total", degreeIds.size());
        res.put("success_count", success.size());
        res.put("errors", errors);
        res.put("data", success);
        return res;
    }

    // --- CÁC HÀM GETTER / VERIFY (Giữ nguyên) ---
    @Override
    public List<Degree> getDegreesByProfile(Long id) {
        return degreeRepository.findByProfileId(id);
    }

    @Override
    public List<Degree> getPendingDegrees() {
        return degreeRepository.findByStatus(DegreeStatus.PENDING);
    }

    @Override
    public Degree getDegreeBySerial(String sn) {
        return degreeRepository.findBySerialNumber(sn);
    }

    @Override
    public DegreeIssueResponse verifyFile(MultipartFile file) {
        String url = blockchainServiceUrl + "/verify-file";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            ByteArrayResource fileRes = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            };
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileRes);
            return restTemplate.postForObject(url, new HttpEntity<>(body, headers), DegreeIssueResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Map<String, Long> getStats() {
        return Map.of(
                "total", degreeRepository.count(),
                "issued", degreeRepository.countByStatus(DegreeStatus.ISSUED),
                "pending", degreeRepository.countByStatus(DegreeStatus.PENDING));
    }
}