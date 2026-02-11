package com.internmatch.backend.dto;

import com.internmatch.backend.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {

    private Long id;

    private Long studentId;
    private String studentEmail;
    private String studentName;

    private Long internshipId;
    private String internshipTitle;
    private String internshipLocation;

    private ApplicationStatus status;
    private String rejectionReason;

    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
