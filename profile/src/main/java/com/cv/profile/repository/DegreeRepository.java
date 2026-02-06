package com.cv.profile.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cv.profile.model.Degree;
import com.cv.profile.model.DegreeStatus;

@Repository
public interface DegreeRepository extends JpaRepository<Degree, Long> {
    List<Degree> findByProfileId(Long profileId);

    Degree findBySerialNumber(String serialNumber);

    long countByStatus(DegreeStatus status);

    List<Degree> findByStatus(DegreeStatus status);
}
