package com.cv.profile.service;

import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;
import com.cv.profile.dto.request.*;
import com.cv.profile.model.*;

public interface AdminService {

    // PROFILE
    void updateGeneralInfo(ProfileUpdateRequest req);

    void importProfileFromCv(MultipartFile file) throws IOException;

    void activateProfile(Long id);

    // PROJECT
    void addProject(ProjectRequest req);

    void updateProject(Long id, ProjectRequest req);

    void deleteProject(Long id);

    Project getProjectById(Long id); // Mới thêm

    // SKILL
    void addSkill(SkillRequest req);

    void deleteSkill(Long id);

    Skill getSkillById(Long id); // Mới thêm

    // EXPERIENCE
    void addExperience(ExperienceRequest req);

    void deleteExperience(Long id);

    Experience getExperienceById(Long id); // Mới thêm

    // EDUCATION
    void addEducation(EducationRequest req);

    void deleteEducation(Long id);

    Education getEducationById(Long id); // Mới thêm

    // PUBLICATION
    void addPublication(PublicationRequest req);

    void deletePublication(Long id);

    Publication getPublicationById(Long id); // Mới thêm

    // EVENT
    void addEvent(EventRequest req);

    void deleteEvent(Long id);

    Event getEventById(Long id); // Mới thêm

    void updateSkill(Long id, SkillRequest req);

    void updateExperience(Long id, ExperienceRequest req);

    void updateEducation(Long id, EducationRequest req);

    void updatePublication(Long id, PublicationRequest req);

    void updateEvent(Long id, EventRequest req);
}