package com.cv.profile.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameVi;
    private String nameEn;

    private String roleVi;
    private String roleEn;

    @Column(columnDefinition = "TEXT")
    private String customer;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String descriptionVi;
    @Column(columnDefinition = "TEXT")
    private String descriptionEn;
    private String techStack;
    // --- SỬA Ở ĐÂY ---
    // URL ảnh thường rất dài, bắt buộc phải là TEXT
    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "project_gallery", joinColumns = @JoinColumn(name = "project_id"))
    // Sửa cả cột image_url trong bảng phụ project_gallery
    @Column(name = "image_url", columnDefinition = "TEXT")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<String> gallery = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String sourceCodeUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @ToString.Exclude
    @JsonIgnore
    private Profile profile;
}