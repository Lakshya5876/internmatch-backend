package com.internmatch.backend.controller;

import com.internmatch.backend.dto.ResumeResponse;
import com.internmatch.backend.service.ResumeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;

    /**
     * Student uploads their resume PDF
     * POST /api/resumes/upload
     * Content-Type: multipart/form-data
     */
    @PostMapping("/upload")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ResumeResponse> uploadResume(
            @RequestParam Long applicationId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) throws IOException {
        ResumeResponse response = resumeService.uploadResume(applicationId, file, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get resume preview (truncated text - first 500 characters)
     * GET /api/resumes/application/{applicationId}/preview
     */
    @GetMapping("/application/{applicationId}/preview")
    @PreAuthorize("hasAnyRole('STUDENT', 'COMPANY')")
    public ResponseEntity<ResumeResponse> getResumePreview(@PathVariable Long applicationId, Authentication authentication) {
        ResumeResponse response = resumeService.getResumePreview(applicationId, authentication);
        return ResponseEntity.ok(response);
    }

    /**
     * Get resume with full extracted text
     * GET /api/resumes/application/{applicationId}
     */
    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'COMPANY')")
    public ResponseEntity<ResumeResponse> getResume(@PathVariable Long applicationId, Authentication authentication) {
        ResumeResponse response = resumeService.getResume(applicationId, authentication);
        return ResponseEntity.ok(response);
    }
}
