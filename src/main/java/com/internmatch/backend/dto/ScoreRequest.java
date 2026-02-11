package com.internmatch.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRequest {

    // Application ID to score
    @NotNull(message = "Application ID is required")
    private Long applicationId;

    // Internship ID (for verification)
    @NotNull(message = "Internship ID is required")
    private Long internshipId;
}
