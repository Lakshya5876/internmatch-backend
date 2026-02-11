package com.internmatch.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreResponse {

    private Long id;

    private Long applicationId;
    private String studentName;
    private String studentEmail;

    private Long internshipId;
    private String internshipTitle;

    // Similarity score (0.0 to 1.0, multiplied by 100 for percentage)
    private Double similarityScore;
    private Integer similarityPercentage;

    // Keyword matching
    private Integer keywordMatches;
    private Integer totalKeywords;

    // Overall explanation
    private String explanation;

    // Rank among applicants for this internship
    private Integer rank;

    private LocalDateTime scoredAt;
}
