package com.cv.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cv.profile.model.LocalOrg;

@Repository
public interface LocalOrgRepository extends JpaRepository<LocalOrg, Long> {
}
