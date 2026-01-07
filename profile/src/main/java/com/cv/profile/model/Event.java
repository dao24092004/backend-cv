package com.cv.profile.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameVi;
    private String nameEn;
    private String role;
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String descriptionVi;
    @Column(columnDefinition = "TEXT")
    private String descriptionEn;
    private String imageUrl; // Cho Zig-zag UI

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private Profile profile;
}