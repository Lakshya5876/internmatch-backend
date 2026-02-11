package com.internmatch.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "resumes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One resume per application
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false, unique = true)
    private Application application;

    // Original filename (what user uploaded)
    @Column(nullable = false)
    private String fileName;

    // File size in bytes
    @Column(nullable = false)
    private Long fileSize;

    // MIME type (should always be application/pdf)
    @Column(nullable = false)
    private String mimeType;

    // Extracted text from PDF (searchable)
    @Column(columnDefinition = "LONGTEXT")
    private String extractedText;

    // Base64 encoded PDF for storage (optional - if storing files in DB)
    // For production, consider storing files in S3/Cloud Storage instead
    @Column(columnDefinition = "LONGBLOB")
    private byte[] fileData;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;

    private LocalDateTime updatedAt;
}
