package com.internmatch.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeRequest {

    // Application ID this resume belongs to
    private Long applicationId;

    // The PDF file uploaded by student
    private MultipartFile file;
}
