package com.cv.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cv.profile.model.Experience;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {}
