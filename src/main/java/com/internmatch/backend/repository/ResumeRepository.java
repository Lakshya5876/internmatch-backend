package com.internmatch.backend.repository;

import com.internmatch.backend.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {

    // Find resume by application ID
    Optional<Resume> findByApplicationId(Long applicationId);

    // Check if resume exists for an application
    boolean existsByApplicationId(Long applicationId);
}
