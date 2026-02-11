package com.internmatch.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "internships")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Internship {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String location;
    
    @Column(nullable = false)
    private String jobType; // FULL_TIME, PART_TIME, REMOTE
    
    private Integer duration; // in months
    private Double stipend;
    
    @Column(columnDefinition = "TEXT")
    private String skills; // Comma-separated: Java,Spring Boot,MySQL
    
    @Column(columnDefinition = "TEXT")
    private String responsibilities;
    
    @Column(columnDefinition = "TEXT")
    private String qualifications;
    
    private LocalDate applicationDeadline;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private User company; // Company that posted this internship
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}