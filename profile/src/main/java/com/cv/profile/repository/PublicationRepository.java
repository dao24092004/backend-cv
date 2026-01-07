package com.cv.profile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cv.profile.model.Publication;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {
}
