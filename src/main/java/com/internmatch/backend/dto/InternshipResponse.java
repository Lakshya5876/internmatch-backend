package com.internmatch.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternshipResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String jobType;
    private Integer duration;
    private Double stipend;
    private String skills;
    private String responsibilities;
    private String qualifications;
    private LocalDate applicationDeadline;
    private Long companyId;
    private String companyName;
    private Boolean active;
    private LocalDateTime createdAt;
}