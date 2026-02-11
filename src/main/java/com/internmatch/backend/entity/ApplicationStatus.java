package com.internmatch.backend.entity;

public enum ApplicationStatus {
    PENDING,      // Student has applied, company hasn't reviewed
    ACCEPTED,     // Company accepted the application
    REJECTED,     // Company rejected the application
    WITHDRAWN     // Student withdrew the application
}
