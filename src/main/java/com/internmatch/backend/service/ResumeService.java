package com.internmatch.backend.service;

import com.internmatch.backend.dto.ResumeResponse;
import com.internmatch.backend.entity.Application;
import com.internmatch.backend.entity.Role;
import com.internmatch.backend.entity.Resume;
import com.internmatch.backend.entity.User;
import com.internmatch.backend.repository.ApplicationRepository;
import com.internmatch.backend.repository.ResumeRepository;
import com.internmatch.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    /**
     * Upload and process resume PDF
     */
    public ResumeResponse uploadResume(Long applicationId, MultipartFile file, Authentication authentication) throws IOException {
        // Validate application exists
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        // Verify current user is the student who applied
        String email = authentication.getName();
        if (!application.getStudent().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only upload resume for your own application");
        }

        // Check if resume already exists for this application
        if (resumeRepository.existsByApplicationId(applicationId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Resume already uploaded for this application. Please delete the old one first.");
        }

        // Validate file is not empty
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        // Validate file is PDF
        String mimeType = file.getContentType();
        if (!isPdfFile(mimeType, file.getOriginalFilename())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF files are allowed");
        }

        // Validate file size (10MB limit)
        long maxFileSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxFileSize) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File size exceeds 10MB limit");
        }

        // Extract text from PDF
        String extractedText = extractTextFromPdf(file);
        if (extractedText == null || extractedText.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Could not extract text from PDF. Please ensure it's a valid PDF with text content.");
        }

        // Create resume record
        Resume resume = new Resume();
        resume.setApplication(application);
        resume.setFileName(file.getOriginalFilename());
        resume.setFileSize(file.getSize());
        resume.setMimeType(mimeType);
        resume.setExtractedText(extractedText);
        resume.setFileData(file.getBytes()); // Store binary data
        resume.setUploadedAt(LocalDateTime.now());
        resume.setUpdatedAt(LocalDateTime.now());

        Resume savedResume = resumeRepository.save(resume);
        return convertToResponse(savedResume, true);
    }

    /**
     * Get resume for an application
     */
    public ResumeResponse getResume(Long applicationId, Authentication authentication) {
        Resume resume = resumeRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found for this application"));
        authorizeResumeAccess(resume, authentication);
        return convertToResponse(resume, true);
    }

    /**
     * Get resume with preview only (truncated text)
     */
    public ResumeResponse getResumePreview(Long applicationId, Authentication authentication) {
        Resume resume = resumeRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found for this application"));
        authorizeResumeAccess(resume, authentication);
        return convertToResponse(resume, false);
    }

    private void authorizeResumeAccess(Resume resume, Authentication authentication) {
        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Long ownerStudentId = resume.getApplication().getStudent().getId();
        Long ownerCompanyId = resume.getApplication().getInternship().getCompany().getId();

        if (currentUser.getRole() == Role.STUDENT && ownerStudentId.equals(currentUser.getId())) {
            return;
        }
        if (currentUser.getRole() == Role.COMPANY && ownerCompanyId.equals(currentUser.getId())) {
            return;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to access this resume");
    }

    /**
     * Extract text from PDF file using PDFBox
     */
    private String extractTextFromPdf(MultipartFile file) {
        try {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(Loader.loadPDF(file.getBytes()));

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error reading PDF: " + e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error processing PDF: " + e.getMessage());
        }
    }

    /**
     * Check if uploaded file is a PDF
     */
    private boolean isPdfFile(String mimeType, String filename) {
        if (mimeType != null && mimeType.equals("application/pdf")) {
            return true;
        }
        if (filename != null && filename.toLowerCase().endsWith(".pdf")) {
            return true;
        }
        return false;
    }

    /**
     * Convert Resume entity to ResumeResponse DTO
     */
    private ResumeResponse convertToResponse(Resume resume, boolean fullText) {
        String extractedText = resume.getExtractedText();
        String preview = extractedText;

        // If not requesting full text, truncate to preview
        if (!fullText && extractedText != null && extractedText.length() > 500) {
            preview = extractedText.substring(0, 500) + "...";
        }

        ResumeResponse response = ResumeResponse.builder()
                .id(resume.getId())
                .applicationId(resume.getApplication().getId())
                .fileName(resume.getFileName())
                .fileSize(resume.getFileSize())
                .mimeType(resume.getMimeType())
                .extractedTextPreview(preview)
                .uploadedAt(resume.getUploadedAt())
                .updatedAt(resume.getUpdatedAt())
                .build();

        // Include full text only if requested
        if (fullText) {
            response.setExtractedText(extractedText);
        }

        return response;
    }
}
