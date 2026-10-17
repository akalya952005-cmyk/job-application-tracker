package com.placement.tracker.service;

import com.placement.tracker.dao.JobApplicationRepository;
import com.placement.tracker.model.JobApplication;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JobApplicationService {
    private static final List<String> STATUSES = List.of(
            "Applied", "Online Assessment", "Interview", "Offered", "Rejected", "Withdrawn");

    private final JobApplicationRepository repository;

    public JobApplicationService(JobApplicationRepository repository) {
        this.repository = repository;
    }

    public List<JobApplication> findAll(String search, String status) throws SQLException {
        String normalizedStatus = status == null ? "" : status.trim();
        if (!normalizedStatus.isEmpty() && !STATUSES.contains(normalizedStatus)) {
            throw new IllegalArgumentException("Choose a valid application status.");
        }
        return repository.findAll(search == null ? "" : search.trim(), normalizedStatus);
    }

    public Optional<JobApplication> findById(String rawId) throws SQLException {
        return repository.findById(parseId(rawId));
    }

    public long create(JobApplication application) throws SQLException {
        validateAndNormalize(application);
        application.setId(0);
        return repository.create(application);
    }

    public boolean update(JobApplication application) throws SQLException {
        validateAndNormalize(application);
        if (application.getId() <= 0) {
            throw new IllegalArgumentException("Enter a valid application ID.");
        }
        return repository.update(application);
    }

    public boolean delete(String rawId) throws SQLException {
        return repository.delete(parseId(rawId));
    }

    public Map<String, Long> getStatusCounts() throws SQLException {
        return repository.getStatusCounts();
    }

    public static List<String> getStatuses() {
        return STATUSES;
    }

    private long parseId(String rawId) {
        try {
            long id = Long.parseLong(rawId);
            if (id <= 0) {
                throw new IllegalArgumentException("Enter a valid application ID.");
            }
            return id;
        } catch (NumberFormatException | NullPointerException exception) {
            throw new IllegalArgumentException("Enter a valid application ID.");
        }
    }

    private void validateAndNormalize(JobApplication application) {
        String companyName = trim(application.getCompanyName());
        String jobRole = trim(application.getJobRole());
        if (companyName.isEmpty()) {
            throw new IllegalArgumentException("Company name is required.");
        }
        if (companyName.length() > 150) {
            throw new IllegalArgumentException("Company name must be 150 characters or fewer.");
        }
        if (jobRole.isEmpty()) {
            throw new IllegalArgumentException("Job role is required.");
        }
        if (jobRole.length() > 150) {
            throw new IllegalArgumentException("Job role must be 150 characters or fewer.");
        }

        application.setCompanyName(companyName);
        application.setJobRole(jobRole);
        application.setStatus(trim(application.getStatus()));
        if (application.getStatus().isEmpty()) {
            application.setStatus("Applied");
        }
        if (!STATUSES.contains(application.getStatus())) {
            throw new IllegalArgumentException("Choose a valid application status.");
        }
        if (application.getAppliedDate() == null) {
            throw new IllegalArgumentException("Applied date is required.");
        }
        if (application.getAppliedDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Applied date cannot be in the future.");
        }
        if (application.getInterviewDate() != null
                && application.getInterviewDate().isBefore(application.getAppliedDate())) {
            throw new IllegalArgumentException("Interview date cannot be before the applied date.");
        }
        application.setNotes(trim(application.getNotes()));
        if (application.getNotes() != null && application.getNotes().length() > 5000) {
            throw new IllegalArgumentException("Notes must be 5000 characters or fewer.");
        }
    }

    public static LocalDate parseDate(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException(fieldName + " must be a valid date.");
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
