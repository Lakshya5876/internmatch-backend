package com.internmatch.backend.service;

import com.internmatch.backend.dto.InternshipRequest;
import com.internmatch.backend.dto.InternshipResponse;
import com.internmatch.backend.entity.Internship;
import com.internmatch.backend.entity.Role;
import com.internmatch.backend.entity.User;
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
public class InternshipService {
    
    private final InternshipRepository internshipRepository;
    private final UserRepository userRepository;
    
    public InternshipResponse createInternship(InternshipRequest request, Authentication authentication) {
        // Get current logged-in company user
        User company = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (company.getRole() != Role.COMPANY) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only companies can create internships");
        }
        
        // Create internship
        Internship internship = new Internship();
        internship.setTitle(request.getTitle());
        internship.setDescription(request.getDescription());
        internship.setLocation(request.getLocation());
        internship.setJobType(request.getJobType());
        internship.setDuration(request.getDuration());
        internship.setStipend(request.getStipend());
        internship.setSkills(request.getSkills());
        internship.setResponsibilities(request.getResponsibilities());
        internship.setQualifications(request.getQualifications());
        internship.setApplicationDeadline(request.getApplicationDeadline());
        internship.setCompany(company);
        internship.setActive(true);
        internship.setCreatedAt(LocalDateTime.now());
        
        Internship saved = internshipRepository.save(internship);
        return mapToResponse(saved);
    }
    
    public List<InternshipResponse> getAllActiveInternships() {
        return internshipRepository.findByActiveTrueOrderByCreatedAtDesc()
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public InternshipResponse getInternshipById(Long id) {
        Internship internship = internshipRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Internship not found"));
        return mapToResponse(internship);
    }
    
    public List<InternshipResponse> getMyInternships(Authentication authentication) {
        User company = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        if (company.getRole() != Role.COMPANY) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only companies can view their internships");
        }
        
        return internshipRepository.findByCompany(company)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    public InternshipResponse updateInternship(Long id, InternshipRequest request, Authentication authentication) {
        Internship internship = internshipRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Internship not found"));
        
        // Verify ownership
        User company = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        
        if (!internship.getCompany().getId().equals(company.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this internship");
        }
        
        // Update fields
        internship.setTitle(request.getTitle());
        internship.setDescription(request.getDescription());
        internship.setLocation(request.getLocation());
        internship.setJobType(request.getJobType());
        internship.setDuration(request.getDuration());
        internship.setStipend(request.getStipend());
        internship.setSkills(request.getSkills());
        internship.setResponsibilities(request.getResponsibilities());
        internship.setQualifications(request.getQualifications());
        internship.setApplicationDeadline(request.getApplicationDeadline());
        internship.setUpdatedAt(LocalDateTime.now());
        
        Internship updated = internshipRepository.save(internship);
        return mapToResponse(updated);
    }
    
    public void deleteInternship(Long id, Authentication authentication) {
        Internship internship = internshipRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Internship not found"));
        
        User company = userRepository.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        
        if (!internship.getCompany().getId().equals(company.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this internship");
        }
        
        // Soft delete
        internship.setActive(false);
        internship.setUpdatedAt(LocalDateTime.now());
        internshipRepository.save(internship);
    }
    
    private InternshipResponse mapToResponse(Internship internship) {
        InternshipResponse response = new InternshipResponse();
        response.setId(internship.getId());
        response.setTitle(internship.getTitle());
        response.setDescription(internship.getDescription());
        response.setLocation(internship.getLocation());
        response.setJobType(internship.getJobType());
        response.setDuration(internship.getDuration());
        response.setStipend(internship.getStipend());
        response.setSkills(internship.getSkills());
        response.setResponsibilities(internship.getResponsibilities());
        response.setQualifications(internship.getQualifications());
        response.setApplicationDeadline(internship.getApplicationDeadline());
        response.setCompanyId(internship.getCompany().getId());
        response.setCompanyName(internship.getCompany().getFullName());
        response.setActive(internship.getActive());
        response.setCreatedAt(internship.getCreatedAt());
        return response;
    }
}
