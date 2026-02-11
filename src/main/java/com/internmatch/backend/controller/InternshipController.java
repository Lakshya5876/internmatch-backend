package com.internmatch.backend.controller;

import com.internmatch.backend.dto.InternshipRequest;
import com.internmatch.backend.dto.InternshipResponse;
import com.internmatch.backend.service.InternshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internships")
@RequiredArgsConstructor
public class InternshipController {
    
    private final InternshipService internshipService;
    
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<InternshipResponse> createInternship(
            @Valid @RequestBody InternshipRequest request,
            Authentication authentication) {
        InternshipResponse response = internshipService.createInternship(request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<InternshipResponse>> getAllInternships() {
        List<InternshipResponse> internships = internshipService.getAllActiveInternships();
        return ResponseEntity.ok(internships);
    }
    
    @GetMapping("/my-internships")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<InternshipResponse>> getMyInternships(Authentication authentication) {
        List<InternshipResponse> internships = internshipService.getMyInternships(authentication);
        return ResponseEntity.ok(internships);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InternshipResponse> getInternshipById(@PathVariable Long id) {
        InternshipResponse internship = internshipService.getInternshipById(id);
        return ResponseEntity.ok(internship);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<InternshipResponse> updateInternship(
            @PathVariable Long id,
            @Valid @RequestBody InternshipRequest request,
            Authentication authentication) {
        InternshipResponse response = internshipService.updateInternship(id, request, authentication);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Void> deleteInternship(@PathVariable Long id, Authentication authentication) {
        internshipService.deleteInternship(id, authentication);
        return ResponseEntity.noContent().build();
    }
}