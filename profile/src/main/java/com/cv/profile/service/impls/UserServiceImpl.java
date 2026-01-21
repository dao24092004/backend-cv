package com.cv.profile.service.impls;

import com.cv.profile.config.SecurityUtils;
import com.cv.profile.dto.ai.CVExtractionResult;
import com.cv.profile.dto.request.*;
import com.cv.profile.mapper.PortfolioMapper;
import com.cv.profile.model.*;
import com.cv.profile.repository.*;
import com.cv.profile.service.CvImageExtractor;
import com.cv.profile.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final ProfileRepository profileRepository;
    private final ProjectRepository projectRepository;
    private final SkillRepository skillRepository;
    private final ExperienceRepository experienceRepository;
    private final EducationRepository educationRepository;
    private final PublicationRepository publicationRepository;
    private final EventRepository eventRepository;

    private final PortfolioMapper mapper;
    private final CvParserServiceImpl cvParserService;
    private final CvImageExtractor cvImageExtractor;

    // --- HELPER: Lấy Profile của User đang đăng nhập ---
    private Profile getCurrentUserProfile() {
        Long currentId = SecurityUtils.getCurrentProfileId();
        return profileRepository.findById(currentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ người dùng (ID: " + currentId + ")"));
    }

    // --- HELPER: Kiểm tra quyền sở hữu (Ownership Check) ---
    private void checkOwnership(Profile owner, Long currentUserId) {
        if (owner == null || !owner.getId().equals(currentUserId)) {
            throw new RuntimeException("Truy cập bị từ chối: Bạn không sở hữu dữ liệu này!");
        }
    }

    // ==========================================================
    // 1. CORE PROFILE
    // ==========================================================
    @Override
    public Profile getMyProfile() {
        return getCurrentUserProfile();
    }

    @Override
    @Transactional
    public void updateMyProfile(ProfileUpdateRequest req) {
        Profile profile = getCurrentUserProfile();
        mapper.updateProfileFromDto(req, profile);
        profileRepository.save(profile);
    }

    @Override
    @Transactional
    public void importMyCv(MultipartFile file) throws IOException {
        Profile profile = getCurrentUserProfile();

        // 1. Phân tích CV
        CVExtractionResult aiData = cvParserService.parseResume(file);

        // 2. Cập nhật thông tin chung
        mapper.updateProfileFromAi(aiData, profile);

        // 3. Cập nhật Avatar nếu có
        String extractedAvatarUrl = cvImageExtractor.extractAvatar(file);
        if (extractedAvatarUrl != null) {
            profile.setAvatarUrl(extractedAvatarUrl);
        }

        // 4. Lưu lại Profile cha trước
        profileRepository.save(profile);
        Profile finalProfile = profile;

        // 5. Thêm mới dữ liệu con từ CV (Append mode)
        // Lưu ý: Logic này sẽ THÊM MỚI vào danh sách cũ, không xóa danh sách cũ.
        if (aiData.workHistory() != null) {
            experienceRepository.saveAll(aiData.workHistory().stream()
                    .map(i -> mapper.toExperienceFromAi(i, finalProfile)).collect(Collectors.toList()));
        }
        if (aiData.projects() != null) {
            projectRepository.saveAll(aiData.projects().stream()
                    .map(i -> mapper.toProjectFromAi(i, finalProfile)).collect(Collectors.toList()));
        }
        if (aiData.skills() != null) {
            skillRepository.saveAll(aiData.skills().stream()
                    .map(i -> mapper.toSkillFromAi(i, finalProfile)).collect(Collectors.toList()));
        }
        if (aiData.education() != null) {
            educationRepository.saveAll(aiData.education().stream()
                    .map(i -> mapper.toEducationFromAi(i, finalProfile)).collect(Collectors.toList()));
        }
        // ... (Tương tự cho Publication/Event nếu AI hỗ trợ)
    }

    // ==========================================================
    // 2. PROJECTS
    // ==========================================================
    @Override
    @Transactional
    public void addProject(ProjectRequest req) {
        Profile profile = getCurrentUserProfile();
        Project project = mapper.toProjectEntity(req);
        project.setProfile(profile);
        if (project.getGallery() == null)
            project.setGallery(new ArrayList<>());
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void updateProject(Long id, ProjectRequest req) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án"));

        // Security Check
        checkOwnership(project.getProfile(), SecurityUtils.getCurrentProfileId());

        mapper.updateProjectEntity(req, project);
        if (req.getGallery() != null)
            project.setGallery(req.getGallery());
        projectRepository.save(project);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dự án"));
        checkOwnership(project.getProfile(), SecurityUtils.getCurrentProfileId());
        projectRepository.delete(project);
    }

    // ==========================================================
    // 3. SKILLS
    // ==========================================================
    @Override
    @Transactional
    public void addSkill(SkillRequest req) {
        Profile profile = getCurrentUserProfile();
        Skill skill = mapper.toSkillEntity(req);
        skill.setProfile(profile);
        skillRepository.save(skill);
    }

    @Override
    @Transactional
    public void updateSkill(Long id, SkillRequest req) {
        Skill skill = skillRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        checkOwnership(skill.getProfile(), SecurityUtils.getCurrentProfileId());
        mapper.updateSkillEntity(req, skill);
        skillRepository.save(skill);
    }

    @Override
    @Transactional
    public void deleteSkill(Long id) {
        Skill skill = skillRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        checkOwnership(skill.getProfile(), SecurityUtils.getCurrentProfileId());
        skillRepository.delete(skill);
    }

    // ==========================================================
    // 4. EXPERIENCES
    // ==========================================================
    @Override
    @Transactional
    public void addExperience(ExperienceRequest req) {
        Profile profile = getCurrentUserProfile();
        Experience exp = mapper.toExperienceEntity(req);
        exp.setProfile(profile);
        experienceRepository.save(exp);
    }

    @Override
    @Transactional
    public void updateExperience(Long id, ExperienceRequest req) {
        Experience exp = experienceRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        checkOwnership(exp.getProfile(), SecurityUtils.getCurrentProfileId());
        mapper.updateExperienceEntity(req, exp);
        experienceRepository.save(exp);
    }

    @Override
    @Transactional
    public void deleteExperience(Long id) {
        Experience exp = experienceRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        checkOwnership(exp.getProfile(), SecurityUtils.getCurrentProfileId());
        experienceRepository.delete(exp);
    }

    // ==========================================================
    // 5. EDUCATIONS
    // ==========================================================
    @Override
    @Transactional
    public void addEducation(EducationRequest req) {
        Profile profile = getCurrentUserProfile();
        Education edu = new Education();
        edu.setSchoolName(req.getSchoolName());
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
        Education edu = educationRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        checkOwnership(edu.getProfile(), SecurityUtils.getCurrentProfileId());
        mapper.updateEducationEntity(req, edu);
        educationRepository.save(edu);
    }

    @Override
    @Transactional
    public void deleteEducation(Long id) {
        Education edu = educationRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        checkOwnership(edu.getProfile(), SecurityUtils.getCurrentProfileId());
        educationRepository.delete(edu);
    }

    // ==========================================================
    // 6. PUBLICATIONS
    // ==========================================================
    @Override
    @Transactional
    public void addPublication(PublicationRequest req) {
        Profile profile = getCurrentUserProfile();
        Publication pub = new Publication();
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
        Publication pub = publicationRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        checkOwnership(pub.getProfile(), SecurityUtils.getCurrentProfileId());
        mapper.updatePublicationEntity(req, pub);
        publicationRepository.save(pub);
    }

    @Override
    @Transactional
    public void deletePublication(Long id) {
        Publication pub = publicationRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        checkOwnership(pub.getProfile(), SecurityUtils.getCurrentProfileId());
        publicationRepository.delete(pub);
    }

    // ==========================================================
    // 7. EVENTS
    // ==========================================================
    @Override
    @Transactional
    public void addEvent(EventRequest req) {
        Profile profile = getCurrentUserProfile();
        Event evt = new Event();
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
        Event evt = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        checkOwnership(evt.getProfile(), SecurityUtils.getCurrentProfileId());
        mapper.updateEventEntity(req, evt);
        eventRepository.save(evt);
    }

    @Override
    @Transactional
    public void deleteEvent(Long id) {
        Event evt = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
        checkOwnership(evt.getProfile(), SecurityUtils.getCurrentProfileId());
        eventRepository.delete(evt);
    }
}