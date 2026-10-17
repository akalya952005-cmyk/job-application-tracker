package com.placement.tracker.dao;

import com.placement.tracker.model.JobApplication;
import com.placement.tracker.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JobApplicationDAO implements JobApplicationRepository {
    private static final String SELECT_COLUMNS =
            "SELECT id, company_name, job_role, applied_date, status, interview_date, notes "
                    + "FROM job_applications";

    @Override
    public List<JobApplication> findAll(String search, String status) throws SQLException {
        StringBuilder sql = new StringBuilder(SELECT_COLUMNS);
        List<String> conditions = new ArrayList<>();
        List<Object> parameters = new ArrayList<>();

        if (search != null && !search.isBlank()) {
            conditions.add("(company_name LIKE ? OR job_role LIKE ?)");
            String searchTerm = "%" + search.trim() + "%";
            parameters.add(searchTerm);
            parameters.add(searchTerm);
        }
        if (status != null && !status.isBlank()) {
            conditions.add("status = ?");
            parameters.add(status);
        }
        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        sql.append(" ORDER BY applied_date DESC, id DESC");

        List<JobApplication> applications = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                statement.setString(i + 1, parameters.get(i).toString());
            }
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    applications.add(mapRow(result));
                }
            }
        }
        return applications;
    }

    @Override
    public Optional<JobApplication> findById(long id) throws SQLException {
        String sql = SELECT_COLUMNS + " WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(mapRow(result)) : Optional.empty();
            }
        }
    }

    @Override
    public long create(JobApplication application) throws SQLException {
        String sql = "INSERT INTO job_applications "
                + "(company_name, job_role, applied_date, status, interview_date, notes) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindApplication(statement, application);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
                throw new SQLException("The database did not return an ID for the new application.");
            }
        }
    }

    @Override
    public boolean update(JobApplication application) throws SQLException {
        String sql = "UPDATE job_applications SET company_name = ?, job_role = ?, applied_date = ?, "
                + "status = ?, interview_date = ?, notes = ? WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindApplication(statement, application);
            statement.setLong(7, application.getId());
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(long id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM job_applications WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    @Override
    public Map<String, Long> getStatusCounts() throws SQLException {
        Map<String, Long> counts = new LinkedHashMap<>();
        String sql = "SELECT status, COUNT(*) AS application_count "
                + "FROM job_applications GROUP BY status";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                counts.put(result.getString("status"), result.getLong("application_count"));
            }
        }
        return counts;
    }

    private void bindApplication(PreparedStatement statement, JobApplication application) throws SQLException {
        statement.setString(1, application.getCompanyName());
        statement.setString(2, application.getJobRole());
        statement.setDate(3, Date.valueOf(application.getAppliedDate()));
        statement.setString(4, application.getStatus());
        if (application.getInterviewDate() == null) {
            statement.setNull(5, java.sql.Types.DATE);
        } else {
            statement.setDate(5, Date.valueOf(application.getInterviewDate()));
        }
        statement.setString(6, application.getNotes());
    }

    private JobApplication mapRow(ResultSet result) throws SQLException {
        Date interviewDate = result.getDate("interview_date");
        return new JobApplication(
                result.getLong("id"),
                result.getString("company_name"),
                result.getString("job_role"),
                result.getDate("applied_date").toLocalDate(),
                result.getString("status"),
                interviewDate == null ? null : interviewDate.toLocalDate(),
                result.getString("notes"));
    }
}
