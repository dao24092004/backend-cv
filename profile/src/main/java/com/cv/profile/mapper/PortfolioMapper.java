package com.cv.profile.mapper;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import com.cv.profile.dto.request.*;
import com.cv.profile.dto.ai.CVExtractionResult;
import com.cv.profile.dto.response.*;
import com.cv.profile.model.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PortfolioMapper {

    // --- HELPER ---
    private String getText(String textVi, String textEn) {
        String lang = LocaleContextHolder.getLocale().getLanguage();
        if ("en".equalsIgnoreCase(lang)) {
            return (textEn != null && !textEn.isBlank()) ? textEn : textVi;
        }
        return textVi;
    }

    // ================= TO DTO (RESPONSE) =================
    // (Giữ nguyên phần này như code cũ)
    public PortfolioDTO toPortfolioDTO(Profile entity) {
        if (entity == null)
            return null;

        List<Experience> experiences = entity.getExperiences() != null ? entity.getExperiences() : new ArrayList<>();
        List<Project> projects = entity.getProjects() != null ? entity.getProjects() : new ArrayList<>();
        List<Skill> skills = entity.getSkills() != null ? entity.getSkills() : new ArrayList<>();
        List<Education> education = entity.getEducation() != null ? entity.getEducation() : new ArrayList<>();
        List<Publication> publications = entity.getPublications() != null ? entity.getPublications()
                : new ArrayList<>();
        List<Event> events = entity.getEvents() != null ? entity.getEvents() : new ArrayList<>();

        return PortfolioDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .avatarUrl(entity.getAvatarUrl())
                .jobTitle(getText(entity.getJobTitleVi(), entity.getJobTitleEn()))
                .bio(getText(entity.getBioVi(), entity.getBioEn()))
                .contact(ContactDTO.builder()
                        .email(entity.getEmail())
                        .phone(entity.getPhone())
                        .address(getText(entity.getAddressVi(), entity.getAddressEn()))
                        .linkedin(entity.getLinkedin())
                        .github(entity.getGithub())
                        .build())
                .workHistory(experiences.stream()
                        .sorted(Comparator.comparing(Experience::getStartDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .map(this::toExperienceDTO).collect(Collectors.toList()))
                .projects(projects.stream().map(this::toProjectDTO).collect(Collectors.toList()))
                .skills(skills.stream().map(this::toSkillDTO).collect(Collectors.toList()))
                .education(education.stream()
                        .sorted(Comparator.comparing(Education::getStartDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .map(this::toEducationDTO).collect(Collectors.toList()))
                .publications(publications.stream()
                        .sorted(Comparator.comparing(Publication::getReleaseDate,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                        .map(this::toPublicationDTO).collect(Collectors.toList()))
                .events(events.stream()
                        .sorted(Comparator.comparing(Event::getDate, Comparator.nullsLast(Comparator.reverseOrder())))
                        .map(this::toEventDTO).collect(Collectors.toList()))
                .build();
    }

    public ExperienceDTO toExperienceDTO(Experience e) {
        return ExperienceDTO.builder()
                .companyName(e.getCompanyName())
                .position(getText(e.getPositionVi(), e.getPositionEn()))
                .description(getText(e.getDescriptionVi(), e.getDescriptionEn()))
                .startDate(e.getStartDate() != null ? e.getStartDate().toString() : null)
                .endDate(e.getIsCurrent() || e.getEndDate() == null ? "Present" : e.getEndDate().toString())
                .build();
    }

    public ProjectDTO toProjectDTO(Project p) {
        return ProjectDTO.builder()
                .id(p.getId())
                .name(getText(p.getNameVi(), p.getNameEn()))
                .role(getText(p.getRoleVi(), p.getRoleEn()))
                .description(getText(p.getDescriptionVi(), p.getDescriptionEn()))
                .imageUrl(p.getImageUrl())
                .sourceCodeUrl(p.getSourceCodeUrl())
                .technologies(p.getTechStack() != null && !p.getTechStack().isEmpty()
                        ? Arrays.asList(p.getTechStack().split(","))
                        : Collections.emptyList())
                .build();
    }

    public SkillDTO toSkillDTO(Skill s) {
        return SkillDTO.builder().name(s.getName()).category(s.getCategory()).proficiency(s.getProficiency()).build();
    }

    public EducationDTO toEducationDTO(Education e) {
        String start = e.getStartDate() != null ? String.valueOf(e.getStartDate().getYear()) : "N/A";
        String end = e.getEndDate() != null ? String.valueOf(e.getEndDate().getYear()) : "Present";
        return EducationDTO.builder().school(e.getSchoolName()).degree(getText(e.getDegreeVi(), e.getDegreeEn()))
                .description(getText(e.getDescriptionVi(), e.getDescriptionEn())).period(start + " - " + end).build();
    }

    public PublicationDTO toPublicationDTO(Publication p) {
        return PublicationDTO.builder().title(getText(p.getTitleVi(), p.getTitleEn())).publisher(p.getPublisher())
                .releaseDate(p.getReleaseDate() != null ? p.getReleaseDate().toString() : null).link(p.getUrl())
                .build();
    }

    public EventDTO toEventDTO(Event e) {
        return EventDTO.builder().name(getText(e.getNameVi(), e.getNameEn())).role(e.getRole())
                .description(getText(e.getDescriptionVi(), e.getDescriptionEn()))
                .date(e.getDate() != null ? e.getDate().toString() : null).imageUrl(e.getImageUrl()).build();
    }

    // ================= FROM AI TO ENTITY (FIXED FULLNAME) =================

    public void updateProfileFromAi(CVExtractionResult aiData, Profile profile) {
        // SỬA Ở ĐÂY: Lấy tên từ Object song ngữ (Ưu tiên tiếng Việt)
        if (aiData.fullName() != null) {
            String name = aiData.fullName().vi();
            if (name == null || name.isEmpty())
                name = aiData.fullName().en();
            profile.setFullName(name);
        }

        if (aiData.email() != null)
            profile.setEmail(aiData.email());
        if (aiData.phone() != null)
            profile.setPhone(aiData.phone());
        if (aiData.linkedin() != null)
            profile.setLinkedin(aiData.linkedin());
        if (aiData.github() != null)
            profile.setGithub(aiData.github());
        if (aiData.avatarUrl() != null)
            profile.setAvatarUrl(aiData.avatarUrl());

        if (aiData.jobTitle() != null) {
            profile.setJobTitleVi(aiData.jobTitle().vi());
            profile.setJobTitleEn(aiData.jobTitle().en());
        }
        if (aiData.bio() != null) {
            profile.setBioVi(aiData.bio().vi());
            profile.setBioEn(aiData.bio().en());
        }
        if (aiData.address() != null) {
            profile.setAddressVi(aiData.address().vi());
            profile.setAddressEn(aiData.address().en());
        }
    }

    // (Các hàm map con giữ nguyên)
    public Project toProjectFromAi(CVExtractionResult.AiProject aiProj, Profile profile) {
        Project p = new Project();
        p.setProfile(profile);
        p.setSourceCodeUrl(aiProj.sourceCodeUrl());
        if (aiProj.name() != null) {
            p.setNameVi(aiProj.name().vi());
            p.setNameEn(aiProj.name().en());
        }
        if (aiProj.role() != null) {
            p.setRoleVi(aiProj.role().vi());
            p.setRoleEn(aiProj.role().en());
        }
        if (aiProj.description() != null) {
            p.setDescriptionVi(aiProj.description().vi());
            p.setDescriptionEn(aiProj.description().en());
        }
        if (aiProj.techStack() != null) {
            p.setTechStack(String.join(", ", aiProj.techStack()));
        }
        p.setGallery(new ArrayList<>());
        return p;
    }

    public Experience toExperienceFromAi(CVExtractionResult.AiExperience aiExp, Profile profile) {
        Experience e = new Experience();
        e.setProfile(profile);
        e.setCompanyName(aiExp.companyName());
        if (aiExp.position() != null) {
            e.setPositionVi(aiExp.position().vi());
            e.setPositionEn(aiExp.position().en());
        }
        if (aiExp.description() != null) {
            e.setDescriptionVi(aiExp.description().vi());
            e.setDescriptionEn(aiExp.description().en());
        }
        e.setStartDate(parseDate(aiExp.startDate()));
        e.setEndDate(parseDate(aiExp.endDate()));
        e.setIsCurrent(aiExp.endDate() == null || aiExp.endDate().equalsIgnoreCase("present"));
        return e;
    }

    public Education toEducationFromAi(CVExtractionResult.AiEducation aiEdu, Profile profile) {
        Education edu = new Education();
        edu.setProfile(profile);
        edu.setSchoolName(aiEdu.school());
        if (aiEdu.degree() != null) {
            edu.setDegreeVi(aiEdu.degree().vi());
            edu.setDegreeEn(aiEdu.degree().en());
        }
        if (aiEdu.description() != null) {
            edu.setDescriptionVi(aiEdu.description().vi());
            edu.setDescriptionEn(aiEdu.description().en());
        }
        edu.setStartDate(parseDate(aiEdu.startDate()));
        edu.setEndDate(parseDate(aiEdu.endDate()));
        return edu;
    }

    public Publication toPublicationFromAi(CVExtractionResult.AiPublication aiPub, Profile profile) {
        Publication p = new Publication();
        p.setProfile(profile);
        p.setPublisher(aiPub.publisher());
        p.setUrl(aiPub.url());
        if (aiPub.name() != null) {
            p.setTitleVi(aiPub.name().vi());
            p.setTitleEn(aiPub.name().en());
        }
        p.setReleaseDate(parseDate(aiPub.date()));
        return p;
    }

    public Event toEventFromAi(CVExtractionResult.AiEvent aiEvt, Profile profile) {
        Event e = new Event();
        e.setProfile(profile);
        e.setRole(aiEvt.role());
        if (aiEvt.name() != null) {
            e.setNameVi(aiEvt.name().vi());
            e.setNameEn(aiEvt.name().en());
        }
        if (aiEvt.description() != null) {
            e.setDescriptionVi(aiEvt.description().vi());
            e.setDescriptionEn(aiEvt.description().en());
        }
        e.setDate(parseDate(aiEvt.date()));
        return e;
    }

    public Skill toSkillFromAi(CVExtractionResult.AiSkill aiSkill, Profile profile) {
        Skill s = new Skill();
        s.setProfile(profile);
        s.setName(aiSkill.name());
        s.setCategory(aiSkill.category() != null ? aiSkill.category() : "General");
        s.setProficiency(aiSkill.proficiency() != null ? aiSkill.proficiency() : 80);
        return s;
    }

    // ================= FROM REQUEST TO ENTITY (MANUAL) =================
    public void updateProfileFromDto(ProfileUpdateRequest req, Profile profile) {
        if (req.getFullName() != null)
            profile.setFullName(req.getFullName());
        if (req.getEmail() != null)
            profile.setEmail(req.getEmail());
        if (req.getPhone() != null)
            profile.setPhone(req.getPhone());
        if (req.getLinkedin() != null)
            profile.setLinkedin(req.getLinkedin());
        if (req.getGithub() != null)
            profile.setGithub(req.getGithub());
        if (req.getAvatarUrl() != null)
            profile.setAvatarUrl(req.getAvatarUrl());
        if (req.getJobTitle() != null)
            profile.setJobTitleVi(req.getJobTitle());
        if (req.getBio() != null)
            profile.setBioVi(req.getBio());
        if (req.getAddress() != null)
            profile.setAddressVi(req.getAddress());
    }

    public Project toProjectEntity(ProjectRequest req) {
        Project p = new Project();
        updateProjectEntity(req, p);
        return p;
    }

    public void updateProjectEntity(ProjectRequest req, Project p) {
        p.setNameVi(req.getName());
        p.setRoleVi(req.getRole());
        p.setDescriptionVi(req.getDescription());
        p.setCustomer(req.getCustomer());
        p.setTechStack(req.getTechStack());
        p.setImageUrl(req.getImageUrl());
        p.setSourceCodeUrl(req.getSourceCodeUrl());
    }

    public Skill toSkillEntity(SkillRequest req) {
        Skill s = new Skill();
        s.setName(req.getName());
        s.setCategory(req.getCategory());
        s.setProficiency(req.getProficiency());
        return s;
    }

    public Experience toExperienceEntity(ExperienceRequest req) {
        Experience e = new Experience();
        e.setCompanyName(req.getCompanyName());
        e.setPositionVi(req.getPosition());
        e.setDescriptionVi(req.getDescription());
        e.setStartDate(req.getStartDate());
        e.setEndDate(req.getEndDate());
        e.setIsCurrent(req.getIsCurrent());
        return e;
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank() || dateStr.equalsIgnoreCase("present"))
            return null;
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateStr + "-01");
            } catch (Exception ex) {
                return null;
            }
        }
    }
}