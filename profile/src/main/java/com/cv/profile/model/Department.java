package com.cv.profile.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_org_id")
    private LocalOrg localOrg;

    @OneToMany(mappedBy = "department")
    @JsonIgnore
    private List<Profile> employees;

    private String code; // Ví dụ: "IT", "HR", "MKT"
}