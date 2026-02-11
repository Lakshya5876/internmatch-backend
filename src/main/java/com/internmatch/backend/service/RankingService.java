package com.internmatch.backend.service;

import com.internmatch.backend.dto.ScoreResponse;
import com.internmatch.backend.entity.*;
import com.internmatch.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final ScoreRepository scoreRepository;
    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final ResumeRepository resumeRepository;

    /**
     * Score an application based on resume-to-job fit
     * Uses: TF-IDF similarity + keyword matching
     */
    public ScoreResponse scoreApplication(Long applicationId, Long internshipId, Authentication authentication) {
        // Get application
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        // Get internship
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Internship not found"));

        // Verify internship matches application
        if (!application.getInternship().getId().equals(internshipId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application does not match internship");
        }

        // Verify internship belongs to current company user
        if (!internship.getCompany().getEmail().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only score applicants for your own internships");
        }

        // Get resume
        Resume resume = resumeRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No resume uploaded for this application"));

        // Extract job description and resume text
        String jobDescription = internship.getTitle() + " " + internship.getDescription() + " " + internship.getSkills();
        String resumeText = resume.getExtractedText();

        // Calculate similarity score (0.0 to 1.0)
        double similarityScore = calculateTextSimilarity(resumeText, jobDescription);

        // Calculate keyword matches
        ScoreMetrics metrics = calculateKeywordMatches(resumeText, internship.getSkills());

        // Generate explanation
        String explanation = generateExplanation(similarityScore, metrics);

        // Save or update score for this application
        Score score = scoreRepository.findByApplicationId(applicationId).orElseGet(Score::new);
        score.setApplication(application);
        score.setInternship(internship);
        score.setSimilarityScore(similarityScore);
        score.setKeywordMatches(metrics.matchCount);
        score.setTotalKeywords(metrics.totalKeywords);
        score.setExplanation(explanation);
        if (score.getScoredAt() == null) {
            score.setScoredAt(LocalDateTime.now());
        }
        score.setUpdatedAt(LocalDateTime.now());

        Score savedScore = scoreRepository.save(score);

        // Set rank (after saving, query all scores for this internship)
        updateRanks(internshipId);

        // Reload to get updated rank
        Score finalScore = scoreRepository.findById(savedScore.getId()).orElse(savedScore);

        return convertToResponse(finalScore);
    }

    /**
     * Get ranked applicants for an internship
     */
    public List<ScoreResponse> getRankedApplicants(Long internshipId, Authentication authentication) {
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Internship not found"));

        if (!internship.getCompany().getEmail().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view rankings for your own internships");
        }

        List<Score> scores = scoreRepository.getRankedApplicants(internshipId);

        // Add rank
        for (int i = 0; i < scores.size(); i++) {
            scores.get(i).setApplicantRank(i + 1);
        }

        return scores.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Calculate text similarity using TF-IDF approach
     * Simple implementation: percentage of job keywords found in resume
     */
    private double calculateTextSimilarity(String resumeText, String jobDescription) {
        if (resumeText == null || jobDescription == null) {
            return 0.0;
        }

        // Tokenize and normalize
        Set<String> resumeWords = tokenizeAndNormalize(resumeText);
        Set<String> jobWords = tokenizeAndNormalize(jobDescription);

        if (jobWords.isEmpty()) {
            return 0.0;
        }

        // Find intersection
        int matchCount = 0;
        for (String word : jobWords) {
            if (resumeWords.contains(word)) {
                matchCount++;
            }
        }

        // Calculate Jaccard similarity
        int unionSize = resumeWords.size() + jobWords.size() - matchCount;
        double jaccardSimilarity = (double) matchCount / unionSize;

        // Boost by keyword frequency in job description
        double frequencyBoost = calculateFrequencyBoost(resumeText, jobWords);

        // Combined score (weight: 60% similarity, 40% frequency)
        return (jaccardSimilarity * 0.6) + (frequencyBoost * 0.4);
    }

    /**
     * Calculate keyword matches from job skills against resume
     */
    private ScoreMetrics calculateKeywordMatches(String resumeText, String skillsString) {
        if (resumeText == null || skillsString == null || skillsString.isEmpty()) {
            return new ScoreMetrics(0, 0);
        }

        // Parse skills (comma-separated)
        List<String> requiredSkills = Arrays.stream(skillsString.split(","))
                .map(s -> s.trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        String resumeLower = resumeText.toLowerCase();

        int matchCount = 0;
        for (String skill : requiredSkills) {
            if (resumeLower.contains(skill)) {
                matchCount++;
            }
        }

        return new ScoreMetrics(matchCount, requiredSkills.size());
    }

    /**
     * Tokenize text and normalize (lowercase, remove short words)
     */
    private Set<String> tokenizeAndNormalize(String text) {
        if (text == null || text.isEmpty()) {
            return new HashSet<>();
        }

        // Simple tokenization (split on non-alphanumeric)
        String[] tokens = text.toLowerCase()
                .split("[^a-z0-9]+");

        return Arrays.stream(tokens)
                .filter(t -> t.length() > 2) // Ignore words < 3 chars
                .collect(Collectors.toSet());
    }

    /**
     * Calculate frequency boost based on important keywords
     */
    private double calculateFrequencyBoost(String resumeText, Set<String> jobKeywords) {
        String resumeLower = resumeText.toLowerCase();
        int totalMatches = 0;

        for (String keyword : jobKeywords) {
            // Count occurrences
            int count = (resumeLower.length() - resumeLower.replace(keyword, "").length()) / keyword.length();
            totalMatches += count;
        }

        // Normalize (cap at 1.0)
        return Math.min(1.0, totalMatches / (double) jobKeywords.size());
    }

    /**
     * Generate human-readable explanation of the score
     */
    private String generateExplanation(double similarityScore, ScoreMetrics metrics) {
        StringBuilder sb = new StringBuilder();

        // Similarity explanation
        int similarityPercent = (int) (similarityScore * 100);
        if (similarityPercent >= 80) {
            sb.append("Excellent match. ");
        } else if (similarityPercent >= 60) {
            sb.append("Good match. ");
        } else if (similarityPercent >= 40) {
            sb.append("Fair match. ");
        } else {
            sb.append("Limited match. ");
        }

        // Skill explanation
        int skillPercent = (metrics.totalKeywords > 0)
            ? (int) ((metrics.matchCount * 100.0) / metrics.totalKeywords)
            : 0;
        sb.append(String.format("Matched %d out of %d required skills (%d%%). ",
            metrics.matchCount, metrics.totalKeywords, skillPercent));

        // Overall recommendation
        if (similarityScore >= 0.7) {
            sb.append("Highly recommended for interview.");
        } else if (similarityScore >= 0.5) {
            sb.append("Recommended for further review.");
        } else {
            sb.append("May require additional screening.");
        }

        return sb.toString();
    }

    /**
     * Update rank for all scores in an internship
     */
    private void updateRanks(Long internshipId) {
        List<Score> scores = scoreRepository.getRankedApplicants(internshipId);
        for (int i = 0; i < scores.size(); i++) {
            scores.get(i).setApplicantRank(i + 1);
            scoreRepository.save(scores.get(i));
        }
    }

    /**
     * Convert Score entity to ScoreResponse
     */
    private ScoreResponse convertToResponse(Score score) {
        int similarityPercent = (int) (score.getSimilarityScore() * 100);

        return ScoreResponse.builder()
                .id(score.getId())
                .applicationId(score.getApplication().getId())
                .studentName(score.getApplication().getStudent().getFullName())
                .studentEmail(score.getApplication().getStudent().getEmail())
                .internshipId(score.getInternship().getId())
                .internshipTitle(score.getInternship().getTitle())
                .similarityScore(score.getSimilarityScore())
                .similarityPercentage(similarityPercent)
                .keywordMatches(score.getKeywordMatches())
                .totalKeywords(score.getTotalKeywords())
                .explanation(score.getExplanation())
                .rank(score.getApplicantRank())
                .scoredAt(score.getScoredAt())
                .build();
    }

    /**
     * Internal class for keyword metrics
     */
    private static class ScoreMetrics {
        int matchCount;
        int totalKeywords;

        ScoreMetrics(int matchCount, int totalKeywords) {
            this.matchCount = matchCount;
            this.totalKeywords = totalKeywords;
        }
    }
}
