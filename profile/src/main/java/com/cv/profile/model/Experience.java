package com.cv.profile.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String positionVi;
    private String positionEn;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;

    @Column(columnDefinition = "TEXT")
    private String descriptionVi;
    @Column(columnDefinition = "TEXT")
    private String descriptionEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    @JsonIgnore
    private Profile profile;
}