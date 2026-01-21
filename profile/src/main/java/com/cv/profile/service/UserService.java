package com.cv.profile.service;

import com.cv.profile.dto.request.*;
import com.cv.profile.model.Profile;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface UserService {
    // 1. Core Profile
    Profile getMyProfile();

    void updateMyProfile(ProfileUpdateRequest req);

    void importMyCv(MultipartFile file) throws IOException;

    // 2. Projects
    void addProject(ProjectRequest req);

    void updateProject(Long id, ProjectRequest req);

    void deleteProject(Long id);

    // 3. Skills
    void addSkill(SkillRequest req);

    void updateSkill(Long id, SkillRequest req);

    void deleteSkill(Long id);

    // 4. Experiences
    void addExperience(ExperienceRequest req);

    void updateExperience(Long id, ExperienceRequest req);

    void deleteExperience(Long id);

    // 5. Educations
    void addEducation(EducationRequest req);

    void updateEducation(Long id, EducationRequest req);

    void deleteEducation(Long id);

    // 6. Publications
    void addPublication(PublicationRequest req);

    void updatePublication(Long id, PublicationRequest req);

    void deletePublication(Long id);

    // 7. Events
    void addEvent(EventRequest req);

    void updateEvent(Long id, EventRequest req);

    void deleteEvent(Long id);
}