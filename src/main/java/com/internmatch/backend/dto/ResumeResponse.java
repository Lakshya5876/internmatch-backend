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
public class ResumeResponse {

    private Long id;

    private Long applicationId;

    private String fileName;

    private Long fileSize;

    private String mimeType;

    // First 500 characters of extracted text (preview)
    private String extractedTextPreview;

    // Full extracted text (optional - only when explicitly requested)
    private String extractedText;

    private LocalDateTime uploadedAt;

    private LocalDateTime updatedAt;
}
