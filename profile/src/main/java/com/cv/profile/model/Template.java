package com.cv.profile.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "templates")
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String templateName;
    @Column(columnDefinition = "TEXT")
    private String htmlContent;
    private boolean isActive;
    private LocalDateTime createdAt = LocalDateTime.now();
}