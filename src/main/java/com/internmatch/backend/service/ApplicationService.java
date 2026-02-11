package com.internmatch.backend.service;

import com.internmatch.backend.dto.ApplicationRequest;
import com.internmatch.backend.dto.ApplicationResponse;
import com.internmatch.backend.entity.Application;
import com.internmatch.backend.entity.ApplicationStatus;
import com.internmatch.backend.entity.Internship;
import com.internmatch.backend.entity.User;
import com.internmatch.backend.repository.ApplicationRepository;
import com.internmatch.backend.repository.InternshipRepository;
import com.internmatch.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;

    /**
     * Student applies to an internship
     */
    public ApplicationResponse applyToInternship(ApplicationRequest request, Authentication authentication) {
        // Get current user (student)
        String email = authentication.getName();
        User student = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // Verify user is a STUDENT
        if (!student.getRole().name().equals("STUDENT")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can apply to internships");
        }

        // Get internship
        Internship internship = internshipRepository.findById(request.getInternshipId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Internship not found"));

        // Check if internship is still active
        if (!internship.getActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This internship is no longer accepting applications");
        }

        // Check for duplicate application (same student can't apply twice)
        applicationRepository.findByStudentIdAndInternshipId(student.getId(), internship.getId())
                .ifPresent(app -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "You have already applied to this internship");
                });

        // Create new application
        Application application = new Application();
        application.setStudent(student);
        application.setInternship(internship);
        application.setStatus(ApplicationStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        Application savedApplication = applicationRepository.save(application);
        return convertToResponse(savedApplication);
    }

    /**
     * Get all applications for internships posted by the current company
     */
    public List<ApplicationResponse> getApplicationsForMyInternships(Authentication authentication) {
        // Get current user (company)
        String email = authentication.getName();
        User company = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // Verify user is a COMPANY
        if (!company.getRole().name().equals("COMPANY")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only companies can view applicants");
        }

        // Get all internships for this company
        List<Internship> myInternships = internshipRepository.findByCompanyId(company.getId());

        // Get all applications for these internships
        return myInternships.stream()
                .flatMap(internship -> applicationRepository.findByInternshipId(internship.getId()).stream())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all applications for a specific internship (company only)
     */
    public List<ApplicationResponse> getApplicantsForInternship(Long internshipId, Authentication authentication) {
        // Get current user (company)
        String email = authentication.getName();
        User company = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // Verify user is a COMPANY
        if (!company.getRole().name().equals("COMPANY")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only companies can view applicants");
        }

        // Get internship and verify it belongs to this company
        Internship internship = internshipRepository.findById(internshipId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Internship not found"));

        if (!internship.getCompany().getId().equals(company.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view applicants for your own internships");
        }

        // Get all applications for this internship
        List<Application> applications = applicationRepository.findByInternshipId(internshipId);
        return applications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Helper: Convert Application entity to ApplicationResponse
     */
    private ApplicationResponse convertToResponse(Application application) {
        return ApplicationResponse.builder()
                .id(application.getId())
                .studentId(application.getStudent().getId())
                .studentEmail(application.getStudent().getEmail())
                .studentName(application.getStudent().getFullName())
                .internshipId(application.getInternship().getId())
                .internshipTitle(application.getInternship().getTitle())
                .internshipLocation(application.getInternship().getLocation())
                .status(application.getStatus())
                .rejectionReason(application.getRejectionReason())
                .appliedAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}
