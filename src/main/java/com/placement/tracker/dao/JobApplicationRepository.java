package com.placement.tracker.dao;

import com.placement.tracker.model.JobApplication;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JobApplicationRepository {
    List<JobApplication> findAll(String search, String status) throws SQLException;

    Optional<JobApplication> findById(long id) throws SQLException;

    long create(JobApplication application) throws SQLException;

    boolean update(JobApplication application) throws SQLException;

    boolean delete(long id) throws SQLException;

    Map<String, Long> getStatusCounts() throws SQLException;
}
