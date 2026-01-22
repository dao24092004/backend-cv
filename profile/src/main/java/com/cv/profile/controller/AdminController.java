package com.cv.profile.controller;

import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.cv.profile.dto.request.*;
import com.cv.profile.dto.response.*;
import com.cv.profile.mapper.PortfolioMapper;
import com.cv.profile.service.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final PortfolioService portfolioService;
    private final PortfolioMapper mapper;

    // --- GET LIST API (FIX 404) ---
    @GetMapping("/list")
    public ResponseEntity<Page<PortfolioDTO>> getAllProfiles(
            @RequestParam(defaultValue = "0") int page, // Trang số mấy (bắt đầu từ 0)
            @RequestParam(defaultValue = "10") int size // Kích thước trang
    ) {
        // Tạo đối tượng Pageable, sắp xếp theo ID giảm dần (mới nhất lên đầu)
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        return ResponseEntity.ok(portfolioService.getAllProfiles(pageable));
    }

    // --- GET DETAILS API ---
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

    // --- PROFILE ---
    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody ProfileUpdateRequest req) {
        adminService.updateGeneralInfo(req);
        return ResponseEntity.ok("Cập nhật thông tin thành công!");
    }

    // --- PROJECTS ---
    @PostMapping("/projects")
    public ResponseEntity<String> addProject(@RequestBody ProjectRequest req) {
        adminService.addProject(req);
        return ResponseEntity.ok("Đã thêm dự án mới!");
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<String> updateProject(@PathVariable Long id, @RequestBody ProjectRequest req) {
        adminService.updateProject(id, req);
        return ResponseEntity.ok("Đã cập nhật dự án!");
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        adminService.deleteProject(id);
        return ResponseEntity.ok("Đã xóa dự án!");
    }

    // --- SKILLS ---
    @PostMapping("/skills")
    public ResponseEntity<String> addSkill(@RequestBody SkillRequest req) {
        adminService.addSkill(req);
        return ResponseEntity.ok("Đã thêm kỹ năng!");
    }

    // [MỚI] API Update Skill
    @PutMapping("/skills/{id}")
    public ResponseEntity<String> updateSkill(@PathVariable Long id, @RequestBody SkillRequest req) {
        adminService.updateSkill(id, req);
        return ResponseEntity.ok("Đã cập nhật kỹ năng!");
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long id) {
        adminService.deleteSkill(id);
        return ResponseEntity.ok("Đã xóa kỹ năng!");
    }

    // --- EXPERIENCES ---
    @PostMapping("/experiences")
    public ResponseEntity<String> addExperience(@RequestBody ExperienceRequest req) {
        adminService.addExperience(req);
        return ResponseEntity.ok("Đã thêm kinh nghiệm!");
    }

    // [MỚI] API Update Experience
    @PutMapping("/experiences/{id}")
    public ResponseEntity<String> updateExperience(@PathVariable Long id, @RequestBody ExperienceRequest req) {
        adminService.updateExperience(id, req);
        return ResponseEntity.ok("Đã cập nhật kinh nghiệm!");
    }

    @DeleteMapping("/experiences/{id}")
    public ResponseEntity<String> deleteExperience(@PathVariable Long id) {
        adminService.deleteExperience(id);
        return ResponseEntity.ok("Đã xóa kinh nghiệm!");
    }

    // --- EDUCATIONS ---
    @PostMapping("/educations")
    public ResponseEntity<String> addEducation(@RequestBody EducationRequest req) {
        adminService.addEducation(req);
        return ResponseEntity.ok("Added Education!");
    }

    // [MỚI] API Update Education
    @PutMapping("/educations/{id}")
    public ResponseEntity<String> updateEducation(@PathVariable Long id, @RequestBody EducationRequest req) {
        adminService.updateEducation(id, req);
        return ResponseEntity.ok("Đã cập nhật học vấn!");
    }

    @DeleteMapping("/educations/{id}")
    public ResponseEntity<String> deleteEducation(@PathVariable Long id) {
        adminService.deleteEducation(id);
        return ResponseEntity.ok("Deleted Education!");
    }

    // --- PUBLICATIONS ---
    @PostMapping("/publications")
    public ResponseEntity<String> addPublication(@RequestBody PublicationRequest req) {
        adminService.addPublication(req);
        return ResponseEntity.ok("Added Publication!");
    }

    // [MỚI] API Update Publication
    @PutMapping("/publications/{id}")
    public ResponseEntity<String> updatePublication(@PathVariable Long id, @RequestBody PublicationRequest req) {
        adminService.updatePublication(id, req);
        return ResponseEntity.ok("Đã cập nhật ấn phẩm!");
    }

    @DeleteMapping("/publications/{id}")
    public ResponseEntity<String> deletePublication(@PathVariable Long id) {
        adminService.deletePublication(id);
        return ResponseEntity.ok("Deleted Publication!");
    }

    // --- EVENTS ---
    @PostMapping("/events")
    public ResponseEntity<String> addEvent(@RequestBody EventRequest req) {
        adminService.addEvent(req);
        return ResponseEntity.ok("Added Event!");
    }

    // [MỚI] API Update Event
    @PutMapping("/events/{id}")
    public ResponseEntity<String> updateEvent(@PathVariable Long id, @RequestBody EventRequest req) {
        adminService.updateEvent(id, req);
        return ResponseEntity.ok("Đã cập nhật sự kiện!");
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        adminService.deleteEvent(id);
        return ResponseEntity.ok("Deleted Event!");
    }

    // --- IMPORT & OTHERS ---
    @PostMapping(value = "/import-cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importCv(@RequestParam("file") MultipartFile file) {
        try {
            adminService.importProfileFromCv(file);
            return ResponseEntity.ok().body("✅ Import thành công!");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Lỗi: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Lỗi hệ thống: " + e.getMessage());
        }
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

    @DeleteMapping("/profile/{id}")
    public ResponseEntity<String> deleteProfile(@PathVariable Long id) {
        try {
            adminService.deleteProfile(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Lỗi: " + e.getMessage());
        }
        return ResponseEntity.ok("Đã xóa hồ sơ!");
    }
}