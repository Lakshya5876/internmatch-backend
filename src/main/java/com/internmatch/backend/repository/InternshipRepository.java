package com.internmatch.backend.repository;

import com.internmatch.backend.entity.Internship;
import com.internmatch.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InternshipRepository extends JpaRepository<Internship, Long> {
    List<Internship> findByCompany(User company);
    List<Internship> findByCompanyId(Long companyId);
    List<Internship> findByActiveTrue();
    List<Internship> findByActiveTrueOrderByCreatedAtDesc();
}