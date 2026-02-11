package com.internmatch.backend.controller;

import com.internmatch.backend.dto.ApplicationRequest;
import com.internmatch.backend.dto.ApplicationResponse;
import com.internmatch.backend.service.ApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    /**
     * Student applies to an internship
     * POST /api/applications/apply
     */
    @PostMapping("/apply")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApplicationResponse> applyToInternship(
            @Valid @RequestBody ApplicationRequest request,
            Authentication authentication) {
        ApplicationResponse response = applicationService.applyToInternship(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Company gets all applications for their internships
     * GET /api/applications/my-internships
     */
    @GetMapping("/my-internships")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsForMyInternships(Authentication authentication) {
        List<ApplicationResponse> applications = applicationService.getApplicationsForMyInternships(authentication);
        return ResponseEntity.ok(applications);
    }

    /**
     * Company gets all applicants for a specific internship
     * GET /api/applications/internship/{internshipId}
     */
    @GetMapping("/internship/{internshipId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<ApplicationResponse>> getApplicantsForInternship(
            @PathVariable Long internshipId,
            Authentication authentication) {
        List<ApplicationResponse> applicants = applicationService.getApplicantsForInternship(internshipId, authentication);
        return ResponseEntity.ok(applicants);
    }
}
