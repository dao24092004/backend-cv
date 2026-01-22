package com.cv.profile.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PortfolioDTO {
    private Long id;
    private String fullName;
    private String jobTitle;
    private String bio;
    private String avatarUrl;
    private ContactDTO contact;
    private String titleVi;
    private String titleEn;

    private String regionName;
    private String localOrgName;
    private String departmentName;
    private Long departmentId;
    // Danh sách đầy đủ
    private List<ExperienceDTO> workHistory;
    private List<ProjectDTO> projects;
    private List<SkillDTO> skills;
    private List<EducationDTO> education;
    private List<PublicationDTO> publications;
    private List<EventDTO> events;
}