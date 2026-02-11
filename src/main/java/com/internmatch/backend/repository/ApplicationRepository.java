package com.internmatch.backend.repository;

import com.internmatch.backend.entity.Application;
import com.internmatch.backend.entity.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // Check if student has already applied to this internship
    Optional<Application> findByStudentIdAndInternshipId(Long studentId, Long internshipId);

    // Get all applications for a specific internship
    List<Application> findByInternshipId(Long internshipId);

    // Get all applications by a student
    List<Application> findByStudentId(Long studentId);

    // Get all applications for an internship with a specific status
    List<Application> findByInternshipIdAndStatus(Long internshipId, ApplicationStatus status);

    // Get count of applications for an internship
    long countByInternshipId(Long internshipId);
}
