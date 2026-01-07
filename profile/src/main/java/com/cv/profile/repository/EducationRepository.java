package com.cv.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cv.profile.model.Education;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
}