package com.cv.profile.service.impls;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cv.profile.config.SecurityUtils;
import com.cv.profile.dto.ai.CVExtractionResult;
import com.cv.profile.dto.request.*;
import com.cv.profile.mapper.PortfolioMapper;
import com.cv.profile.model.*;
import com.cv.profile.repository.*;
import com.cv.profile.service.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final ProfileRepository profileRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final PublicationRepository publicationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    private final DepartmentRepository departmentRepository;
    private final LocalOrgRepository localOrgRepository;
    private final RegionRepository regionRepository;

    private final PortfolioMapper mapper;
    private final CvParserServiceImpl cvParserService;
    private final SimpMessagingTemplate messagingTemplate;
    private final CvImageExtractor cvImageExtractor;

    // --- HELPER: Lấy Profile chính (hoặc tạo cơ chế chọn Profile nếu cần) ---
    private Profile getMainProfile() {
        return profileRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("Profile not found. Please import CV or create one first."));
    }

    @Override
    @Transactional
    public void importProfileFromCv(MultipartFile file) throws IOException {
        // 1. Gọi AI để phân tích và dịch thuật song ngữ
        CVExtractionResult aiData = cvParserService.parseResume(file);
        String extractedAvatarUrl = cvImageExtractor.extractAvatar(file);
        Profile profile = new Profile();

        // 2. Kiểm tra trùng lặp Email (Update người cũ hoặc Tạo mới)
        if (aiData.email() != null && !aiData.email().isEmpty()) {
            Optional<Profile> existing = profileRepository.findByEmail(aiData.email());
            if (existing.isPresent()) {
                profile = existing.get();
            }
        }

        // 3. Map dữ liệu từ AI vào Entity (Song ngữ)
        mapper.updateProfileFromAi(aiData, profile);

        // Fallback: Nếu AI không tìm thấy tên
        if (profile.getFullName() == null || profile.getFullName().isEmpty()) {
            profile.setFullName("Candidate " + LocalDate.now());
        }

        // Lưu Profile cha trước để lấy ID
        profile = profileRepository.save(profile);
        Profile finalProfile = profile; // Biến final để dùng trong stream

        // 4. Lưu các danh sách con (Relation One-To-Many)

        // Experience
        if (aiData.workHistory() != null) {
            experienceRepository.saveAll(aiData.workHistory().stream()
                    .map(i -> mapper.toExperienceFromAi(i, finalProfile))
                    .collect(Collectors.toList()));
        }

        // Projects
        if (aiData.projects() != null) {
            projectRepository.saveAll(aiData.projects().stream()
                    .map(i -> mapper.toProjectFromAi(i, finalProfile))
                    .collect(Collectors.toList()));
        }

        // Skills
        if (aiData.skills() != null) {
            skillRepository.saveAll(aiData.skills().stream()
                    .map(i -> mapper.toSkillFromAi(i, finalProfile))
                    .collect(Collectors.toList()));
        }

        // Education
        if (aiData.education() != null) {
            educationRepository.saveAll(aiData.education().stream()
                    .map(i -> mapper.toEducationFromAi(i, finalProfile))
                    .collect(Collectors.toList()));
        }

        // Publications
        if (aiData.publications() != null) {
            publicationRepository.saveAll(aiData.publications().stream()
                    .map(i -> mapper.toPublicationFromAi(i, finalProfile))
                    .collect(Collectors.toList()));
        }

        // Events
        if (aiData.events() != null) {
            eventRepository.saveAll(aiData.events().stream()
                    .map(i -> mapper.toEventFromAi(i, finalProfile))
                    .collect(Collectors.toList()));
        }

        if (extractedAvatarUrl != null) {
            profile.setAvatarUrl(extractedAvatarUrl);
        } else if (profile.getAvatarUrl() == null) {
            // Ảnh mặc định
            profile.setAvatarUrl("https://placehold.co/400?text=" +
                    (profile.getFullName() != null ? profile.getFullName().charAt(0) : "A"));
        }
    }

    // ========================================================================
    // 2. PROFILE GENERAL INFO
    // ========================================================================
    @Override
    @Transactional
    public void updateGeneralInfo(ProfileUpdateRequest req) {
        // KIỂM TRA ID
        if (req.getId() == null) {
            throw new RuntimeException("Lỗi: Không tìm thấy ID của hồ sơ cần cập nhật!");
        }

        // TÌM ĐÚNG PROFILE THEO ID
        Profile profile = profileRepository.findById(req.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ với ID: " + req.getId()));

        // Cập nhật dữ liệu
        mapper.updateProfileFromDto(req, profile);

        // Handle flexible organization assignment
        if (req.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(req.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Department ID not found: " + req.getDepartmentId()));
            profile.setDepartment(dept);
            // Also set parent levels for consistency
            profile.setLocalOrg(dept.getLocalOrg());
            profile.setRegion(dept.getLocalOrg() != null ? dept.getLocalOrg().getRegion() : null);
        } else if (req.getLocalOrgId() != null) {
            LocalOrg localOrg = localOrgRepository.findById(req.getLocalOrgId())
                    .orElseThrow(() -> new RuntimeException("LocalOrg ID not found: " + req.getLocalOrgId()));
            profile.setLocalOrg(localOrg);
            profile.setRegion(localOrg.getRegion());
            profile.setDepartment(null); // Clear department if assigning to local org level
        } else if (req.getRegionId() != null) {
            Region region = regionRepository.findById(req.getRegionId())
                    .orElseThrow(() -> new RuntimeException("Region ID not found: " + req.getRegionId()));
            profile.setRegion(region);
            profile.setLocalOrg(null); // Clear lower levels
            profile.setDepartment(null);
        }

        // Lưu lại
        profileRepository.save(profile);
    }

    @Override
    @Transactional
    public void activateProfile(Long id) {
        // 1. Tắt active tất cả người cũ
        List<Profile> all = profileRepository.findAll();
        for (Profile p : all) {
            p.setIsActive(false);
        }
        profileRepository.saveAll(all);

        // 2. Bật active cho người mới
        Profile target = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + id));
        target.setIsActive(true);
        profileRepository.save(target);

        // 3. [QUAN TRỌNG] Gửi thông báo cho Frontend biết để reload
        System.out.println("🔔 Sending Socket Event: PROFILE_UPDATED");
        try {
            // Gửi tin nhắn đến topic mà Home.tsx đang lắng nghe
            messagingTemplate.convertAndSend("/topic/public/updates", "PROFILE_UPDATED");
        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi Socket: " + e.getMessage());
        }
    }

    // ========================================================================
    // 3. PROJECT CRUD
    // ========================================================================
    @Override
    @Transactional
    public void addProject(ProjectRequest req) {
        Profile profile = getMainProfile();
        Project project = mapper.toProjectEntity(req); // Map vào cột _vi mặc định
        project.setProfile(profile);
        if (project.getGallery() == null)
            project.setGallery(new ArrayList<>());
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void updateProject(Long id, ProjectRequest req) {
        Project project = getProjectById(id);
        mapper.updateProjectEntity(req, project);
        if (req.getGallery() != null)
            project.setGallery(req.getGallery());
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public Project getProjectById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found: " + id));
    }

    @Override
    @Transactional
    public void addSkill(SkillRequest req) {
        Long idProfile = SecurityUtils.getCurrentProfileId();
        Profile profile = getMainProfile();
        log.info("Adding skill to profile ID: " + profile.getId());
        Skill skill = mapper.toSkillEntity(req);
        skill.setProfile(profile);
        skillRepository.save(skill);
    }

    @Override
    @Transactional
    public void deleteSkill(Long id) {
        skillRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateSkill(Long id, SkillRequest req) {
        Skill skill = getSkillById(id);
        mapper.updateSkillEntity(req, skill); // Hàm mới thêm ở Mapper
        skillRepository.save(skill);
    }

    @Override
    public Skill getSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found: " + id));
    }

    // ========================================================================
    // 5. EXPERIENCE CRUD
    // ========================================================================
    @Override
    @Transactional
    public void addExperience(ExperienceRequest req) {
        Profile profile = getMainProfile();
        Experience exp = mapper.toExperienceEntity(req); // Map vào _vi mặc định
        exp.setProfile(profile);
        experienceRepository.save(exp);
    }

    @Override
    @Transactional
    public void updateExperience(Long id, ExperienceRequest req) {
        Experience exp = getExperienceById(id);
        mapper.updateExperienceEntity(req, exp); // Hàm mới thêm ở Mapper
        experienceRepository.save(exp);
    }

    @Override
    @Transactional
    public void deleteExperience(Long id) {
        experienceRepository.deleteById(id);
    }

    @Override
    public Experience getExperienceById(Long id) {
        return experienceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found: " + id));
    }

    // ========================================================================
    // 6. EDUCATION CRUD
    // ========================================================================
    @Override
    @Transactional
    public void addEducation(EducationRequest req) {
        Profile profile = getMainProfile();
        Education edu = new Education();
        edu.setSchoolName(req.getSchoolName());

        // Lưu thủ công vào cột Tiếng Việt (Mặc định)
        edu.setDegreeVi(req.getDegree());
        edu.setDescriptionVi(req.getDescription());

        edu.setStartDate(req.getStartDate());
        edu.setEndDate(req.getEndDate());
        edu.setProfile(profile);
        educationRepository.save(edu);
    }

    @Override
    @Transactional
    public void updateEducation(Long id, EducationRequest req) {
        Education edu = getEducationById(id);
        mapper.updateEducationEntity(req, edu); // Hàm mới thêm ở Mapper
        educationRepository.save(edu);
    }

    @Override
    @Transactional
    public void deleteEducation(Long id) {
        educationRepository.deleteById(id);
    }

    @Override
    public Education getEducationById(Long id) {
        return educationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found: " + id));
    }

    // ========================================================================
    // 7. PUBLICATION CRUD
    // ========================================================================
    @Override
    @Transactional
    public void addPublication(PublicationRequest req) {
        Profile profile = getMainProfile();
        Publication pub = new Publication();

        // Lưu thủ công vào cột Tiếng Việt
        pub.setTitleVi(req.getTitle());

        pub.setPublisher(req.getPublisher());
        pub.setReleaseDate(req.getReleaseDate());
        pub.setUrl(req.getUrl());
        pub.setProfile(profile);
        publicationRepository.save(pub);
    }

    @Override
    @Transactional
    public void updatePublication(Long id, PublicationRequest req) {
        Publication pub = getPublicationById(id);
        mapper.updatePublicationEntity(req, pub);
        publicationRepository.save(pub);
    }

    @Override
    @Transactional
    public void deletePublication(Long id) {
        publicationRepository.deleteById(id);
    }

    @Override
    public Publication getPublicationById(Long id) {
        return publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication not found: " + id));
    }

    // ========================================================================
    // 8. EVENT CRUD
    // ========================================================================
    @Override
    @Transactional
    public void addEvent(EventRequest req) {
        Profile profile = getMainProfile();
        Event evt = new Event();

        // Lưu thủ công vào cột Tiếng Việt
        evt.setNameVi(req.getName());
        evt.setDescriptionVi(req.getDescription());

        evt.setRole(req.getRole());
        evt.setDate(req.getDate());
        evt.setProfile(profile);
        eventRepository.save(evt);
    }

    @Override
    @Transactional
    public void updateEvent(Long id, EventRequest req) {
        Event evt = getEventById(id);
        mapper.updateEventEntity(req, evt);
        eventRepository.save(evt);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
    }

    @Override
    @Transactional
    public void deleteProfile(Long id) {
        Optional<User> userOptional = userRepository.findByProfileId(id); // Cần thêm hàm này vào UserRepository

        if (userOptional.isPresent()) {

            userRepository.delete(userOptional.get());
        } else {
            profileRepository.deleteById(id);
        }
    }
}