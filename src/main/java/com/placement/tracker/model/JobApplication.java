package com.placement.tracker.model;

import java.time.LocalDate;

public class JobApplication {
    private long id;
    private String companyName;
    private String jobRole;
    private LocalDate appliedDate;
    private String status;
    private LocalDate interviewDate;
    private String notes;

    public JobApplication() {
    }

    public JobApplication(long id, String companyName, String jobRole, LocalDate appliedDate,
                          String status, LocalDate interviewDate, String notes) {
        this.id = id;
        this.companyName = companyName;
        this.jobRole = jobRole;
        this.appliedDate = appliedDate;
        this.status = status;
        this.interviewDate = interviewDate;
        this.notes = notes;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getJobRole() {
        return jobRole;
    }

    public void setJobRole(String jobRole) {
        this.jobRole = jobRole;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDate appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getInterviewDate() {
        return interviewDate;
    }

    public void setInterviewDate(LocalDate interviewDate) {
        this.interviewDate = interviewDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
