package com.internmatch.backend.controller;

import com.internmatch.backend.dto.ScoreRequest;
import com.internmatch.backend.dto.ScoreResponse;
import com.internmatch.backend.service.RankingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final RankingService rankingService;

    /**
     * Score an application (Company calculates AI ranking)
     * POST /api/scores/calculate
     */
    @PostMapping("/calculate")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ScoreResponse> calculateScore(
            @Valid @RequestBody ScoreRequest request,
            Authentication authentication) {
        ScoreResponse response = rankingService.scoreApplication(
                request.getApplicationId(),
                request.getInternshipId(),
                authentication
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get ranked applicants for an internship
     * GET /api/scores/internship/{internshipId}/ranked
     */
    @GetMapping("/internship/{internshipId}/ranked")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<ScoreResponse>> getRankedApplicants(
            @PathVariable Long internshipId,
            Authentication authentication) {
        List<ScoreResponse> rankedApplicants = rankingService.getRankedApplicants(internshipId, authentication);
        return ResponseEntity.ok(rankedApplicants);
    }
}
