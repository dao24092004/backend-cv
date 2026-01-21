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
import com.cv.profile.dto.response.*;
import com.cv.profile.mapper.PortfolioMapper;
import com.cv.profile.model.Profile;
import com.cv.profile.service.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService; // Dùng Service này cho logic User
    private final AdminService adminService; // Dùng Service này cho các hàm Get Detail / Activate
    private final PortfolioMapper mapper;

    // ========================================================================
    // 1. GET DETAILS (Read-only)
    // Các hàm này vẫn dùng adminService để lấy dữ liệu theo ID cụ thể
    // ========================================================================
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

    // ========================================================================
    // 2. CORE PROFILE (SỬ DỤNG USER SERVICE)
    // ========================================================================

    @GetMapping("/profile")
    public ResponseEntity<List<PortfolioDTO>> getMyProfile() {
        // userService.getMyProfile() tự động lấy ID từ Token
        Profile profile = userService.getMyProfile();
        PortfolioDTO dto = mapper.toPortfolioDTO(profile);
        return ResponseEntity.ok(List.of(dto));
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody ProfileUpdateRequest req) {
        // userService tự xác định user đang login để update
        userService.updateMyProfile(req);
        return ResponseEntity.ok("Cập nhật thông tin thành công!");
    }

    @PostMapping(value = "/import-cv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importCv(@RequestParam("file") MultipartFile file) {
        try {
            // Import thẳng vào profile của user đang login
            userService.importMyCv(file);
            return ResponseEntity.ok().body("✅ Import thành công! Hồ sơ đã được cập nhật.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Lỗi đọc file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("❌ Lỗi hệ thống: " + e.getMessage());
        }
    }

    // ========================================================================
    // 3. SUB-ENTITIES CRUD (SỬ DỤNG USER SERVICE)
    // userService sẽ tự gán Profile ID của người đang login
    // ========================================================================

    // --- PROJECTS ---
    @PostMapping("/projects")
    public ResponseEntity<String> addProject(@RequestBody ProjectRequest req) {
        userService.addProject(req);
        return ResponseEntity.ok("Đã thêm dự án mới!");
    }

    @PutMapping("/projects/{id}")
    public ResponseEntity<String> updateProject(@PathVariable Long id, @RequestBody ProjectRequest req) {
        userService.updateProject(id, req);
        return ResponseEntity.ok("Đã cập nhật dự án!");
    }

    @DeleteMapping("/projects/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable Long id) {
        userService.deleteProject(id);
        return ResponseEntity.ok("Đã xóa dự án!");
    }

    // --- SKILLS ---
    @PostMapping("/skills")
    public ResponseEntity<String> addSkill(@RequestBody SkillRequest req) {
        userService.addSkill(req);
        return ResponseEntity.ok("Đã thêm kỹ năng!");
    }

    @PutMapping("/skills/{id}")
    public ResponseEntity<String> updateSkill(@PathVariable Long id, @RequestBody SkillRequest req) {
        userService.updateSkill(id, req);
        return ResponseEntity.ok("Đã cập nhật kỹ năng!");
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<String> deleteSkill(@PathVariable Long id) {
        userService.deleteSkill(id);
        return ResponseEntity.ok("Đã xóa kỹ năng!");
    }

    // --- EXPERIENCES ---
    @PostMapping("/experiences")
    public ResponseEntity<String> addExperience(@RequestBody ExperienceRequest req) {
        userService.addExperience(req);
        return ResponseEntity.ok("Đã thêm kinh nghiệm!");
    }

    @PutMapping("/experiences/{id}")
    public ResponseEntity<String> updateExperience(@PathVariable Long id, @RequestBody ExperienceRequest req) {
        userService.updateExperience(id, req);
        return ResponseEntity.ok("Đã cập nhật kinh nghiệm!");
    }

    @DeleteMapping("/experiences/{id}")
    public ResponseEntity<String> deleteExperience(@PathVariable Long id) {
        userService.deleteExperience(id);
        return ResponseEntity.ok("Đã xóa kinh nghiệm!");
    }

    // --- EDUCATIONS ---
    @PostMapping("/educations")
    public ResponseEntity<String> addEducation(@RequestBody EducationRequest req) {
        userService.addEducation(req);
        return ResponseEntity.ok("Đã thêm học vấn!");
    }

    @PutMapping("/educations/{id}")
    public ResponseEntity<String> updateEducation(@PathVariable Long id, @RequestBody EducationRequest req) {
        userService.updateEducation(id, req);
        return ResponseEntity.ok("Đã cập nhật học vấn!");
    }

    @DeleteMapping("/educations/{id}")
    public ResponseEntity<String> deleteEducation(@PathVariable Long id) {
        userService.deleteEducation(id);
        return ResponseEntity.ok("Đã xóa học vấn!");
    }

    // --- PUBLICATIONS ---
    @PostMapping("/publications")
    public ResponseEntity<String> addPublication(@RequestBody PublicationRequest req) {
        userService.addPublication(req);
        return ResponseEntity.ok("Đã thêm ấn phẩm!");
    }

    @PutMapping("/publications/{id}")
    public ResponseEntity<String> updatePublication(@PathVariable Long id, @RequestBody PublicationRequest req) {
        userService.updatePublication(id, req);
        return ResponseEntity.ok("Đã cập nhật ấn phẩm!");
    }

    @DeleteMapping("/publications/{id}")
    public ResponseEntity<String> deletePublication(@PathVariable Long id) {
        userService.deletePublication(id);
        return ResponseEntity.ok("Đã xóa ấn phẩm!");
    }

    // --- EVENTS ---
    @PostMapping("/events")
    public ResponseEntity<String> addEvent(@RequestBody EventRequest req) {
        userService.addEvent(req);
        return ResponseEntity.ok("Đã thêm sự kiện!");
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<String> updateEvent(@PathVariable Long id, @RequestBody EventRequest req) {
        userService.updateEvent(id, req);
        return ResponseEntity.ok("Đã cập nhật sự kiện!");
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable Long id) {
        userService.deleteEvent(id);
        return ResponseEntity.ok("Đã xóa sự kiện!");
    }

    // ========================================================================
    // 4. OTHER UTILS
    // ========================================================================

    @GetMapping("/portfolio/{id}")
    public ResponseEntity<?> getProfileDetail(@PathVariable Long id) {
        try {
            // Cho phép xem chi tiết profile người khác (Public view)
            PortfolioDTO dto = mapper.toPortfolioDTO(adminService.getProjectById(id).getProfile());
            // Hoặc dùng: portfolioService.getPortfolioById(id)
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