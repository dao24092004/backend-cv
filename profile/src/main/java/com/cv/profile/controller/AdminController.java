package com.cv.profile.controller;

import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cv.profile.dto.request.*;
import com.cv.profile.dto.response.*; // Import DTO Response
import com.cv.profile.mapper.PortfolioMapper; // Import Mapper
import com.cv.profile.service.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final PortfolioService portfolioService;
    private final PortfolioMapper mapper; // Inject Mapper

    // --- GET DETAILS API (MỚI THÊM) ---

    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toProjectDTO(adminService.getProjectById(id)));
    }

    @GetMapping("/skills/{id}")
    public ResponseEntity<SkillDTO> getSkill(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toSkillDTO(adminService.getSkillById(id)));
    }

    @GetMapping("/experiences/{id}")
    public ResponseEntity<ExperienceDTO> getExperience(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toExperienceDTO(adminService.getExperienceById(id)));
    }

    @GetMapping("/educations/{id}")
    public ResponseEntity<EducationDTO> getEducation(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toEducationDTO(adminService.getEducationById(id)));
    }

    @GetMapping("/publications/{id}")
    public ResponseEntity<PublicationDTO> getPublication(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toPublicationDTO(adminService.getPublicationById(id)));
    }

    @GetMapping("/events/{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.toEventDTO(adminService.getEventById(id)));
    }

    // --- CÁC API CŨ (GIỮ NGUYÊN) ---

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody ProfileUpdateRequest req) {
        adminService.updateGeneralInfo(req);
        return ResponseEntity.ok("Cập nhật thông tin thành công!");
    }

    @PostMapping("/projects")
    public ResponseEntity<String> addProject(@RequestBody ProjectRequest req) {
        adminService.addProject(req);
        return ResponseEntity.ok("Đã thêm dự án mới!");
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<String> updateProject(@PathVariable Long id, @RequestBody ProjectRequest req) {
        adminService.updateProject(id, req);
        return ResponseEntity.ok("Đã cập, nhật dự án!");
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        adminService.deleteProject(id);
        return ResponseEntity.ok("Đã xóa dự án!");
    }

    @PostMapping("/skills")
    public ResponseEntity<String> addSkill(@RequestBody SkillRequest req) {
        adminService.addSkill(req);
        return ResponseEntity.ok("Đã thêm kỹ năng!");
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long id) {
        adminService.deleteSkill(id);
        return ResponseEntity.ok("Đã xóa kỹ năng!");
    }

    @PostMapping("/experiences")
    public ResponseEntity<String> addExperience(@RequestBody ExperienceRequest req) {
        adminService.addExperience(req);
        return ResponseEntity.ok("Đã thêm kinh nghiệm!");
    }

    @DeleteMapping("/experiences/{id}")
    public ResponseEntity<String> deleteExperience(@PathVariable Long id) {
        adminService.deleteExperience(id);
        return ResponseEntity.ok("Đã xóa kinh nghiệm!");
    }

    @PostMapping("/educations")
    public ResponseEntity<String> addEducation(@RequestBody EducationRequest req) {
        adminService.addEducation(req);
        return ResponseEntity.ok("Added Education!");
    }

    @DeleteMapping("/educations/{id}")
    public ResponseEntity<String> deleteEducation(@PathVariable Long id) {
        adminService.deleteEducation(id);
        return ResponseEntity.ok("Deleted Education!");
    }

    @PostMapping("/publications")
    public ResponseEntity<String> addPublication(@RequestBody PublicationRequest req) {
        adminService.addPublication(req);
        return ResponseEntity.ok("Added Publication!");
    }

    @DeleteMapping("/publications/{id}")
    public ResponseEntity<String> deletePublication(@PathVariable Long id) {
        adminService.deletePublication(id);
        return ResponseEntity.ok("Deleted Publication!");
    }

    @PostMapping("/events")
    public ResponseEntity<String> addEvent(@RequestBody EventRequest req) {
        adminService.addEvent(req);
        return ResponseEntity.ok("Added Event!");
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        adminService.deleteEvent(id);
        return ResponseEntity.ok("Deleted Event!");
    }

    @PostMapping(value = "/import-cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importCv(@RequestParam("file") MultipartFile file) {
        try {
            adminService.importProfileFromCv(file);
            return ResponseEntity.ok().body("✅ Import thành công! Một hồ sơ mới đã được tạo.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Lỗi đọc file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Lỗi hệ thống: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<PortfolioDTO>> getAllProfiles() {
        return ResponseEntity.ok(portfolioService.getAllProfiles());
    }

    @GetMapping("/portfolio/{id}")
    public ResponseEntity<?> getProfileDetail(@PathVariable Long id) {
        try {
            PortfolioDTO dto = portfolioService.getPortfolioById(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/profile/{id}/activate")
    public ResponseEntity<String> activateProfile(@PathVariable Long id) {
        adminService.activateProfile(id);
        return ResponseEntity.ok("Đã kích hoạt hồ sơ này hiển thị ra trang chủ!");
    }
}