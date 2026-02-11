package com.internmatch.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternshipRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotBlank(message = "Job type is required")
    private String jobType;
    
    private Integer duration;
    private Double stipend;
    private String skills;
    private String responsibilities;
    private String qualifications;
    
    @NotNull(message = "Application deadline is required")
    private LocalDate applicationDeadline;
}