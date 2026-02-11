package com.internmatch.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "internship_id", nullable = false)
    private Internship internship;

    // Similarity score (0.0 to 1.0)
    @Column(nullable = false)
    private Double similarityScore;

    // Keyword match count
    @Column(nullable = false)
    private Integer keywordMatches;

    // Total keywords in job description
    @Column(nullable = false)
    private Integer totalKeywords;

    // Explanation of the score
    @Column(columnDefinition = "TEXT")
    private String explanation;

    // Overall rank (1st, 2nd, 3rd place among applicants for this internship)
    @Column(name = "applicant_rank")
    private Integer applicantRank;

    @Column(nullable = false)
    private LocalDateTime scoredAt;

    private LocalDateTime updatedAt;
}
