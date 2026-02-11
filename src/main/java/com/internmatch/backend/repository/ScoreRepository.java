package com.internmatch.backend.repository;

import com.internmatch.backend.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    // Get score for a specific application
    Optional<Score> findByApplicationId(Long applicationId);

    // Get all scores for an internship, ordered by similarity score (descending)
    List<Score> findByInternshipIdOrderBySimilarityScoreDesc(Long internshipId);

    // Get all scores for an internship, with explicit rank
    @Query("SELECT s FROM Score s WHERE s.internship.id = ?1 ORDER BY s.similarityScore DESC")
    List<Score> getRankedApplicants(Long internshipId);

    // Check if score already exists
    boolean existsByApplicationId(Long applicationId);

    // Get top N applicants for an internship
    @Query(value = "SELECT * FROM scores WHERE internship_id = ?1 ORDER BY similarity_score DESC LIMIT ?2", nativeQuery = true)
    List<Score> getTopApplicants(Long internshipId, Integer limit);
}
