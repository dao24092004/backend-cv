package com.cv.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cv.profile.model.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
}