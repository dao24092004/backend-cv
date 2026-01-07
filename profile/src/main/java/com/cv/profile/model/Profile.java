package com.cv.profile.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private String linkedin;
    private String github;

    // --- SONG NGỮ ---
    private String jobTitleVi;
    private String jobTitleEn;

    private String addressVi;
    private String addressEn;

    @Column(columnDefinition = "TEXT")
    private String bioVi;
    @Column(columnDefinition = "TEXT")
    private String bioEn;
    private Boolean isActive = false;
    // Relationships
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Experience> experiences = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Skill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Education> education = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Publication> publications = new ArrayList<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL)
    private List<Event> events = new ArrayList<>();
}