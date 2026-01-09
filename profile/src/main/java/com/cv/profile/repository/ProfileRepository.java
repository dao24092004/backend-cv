package com.cv.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cv.profile.model.Profile;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByIsActiveTrue();

    @EntityGraph(attributePaths = { "department", "department.localOrg", "department.localOrg.region" })
    Optional<Profile> findById(Long id);
}