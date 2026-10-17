package com.placement.tracker.servlet;

import com.placement.tracker.dao.JobApplicationDAO;
import com.placement.tracker.model.JobApplication;
import com.placement.tracker.service.JobApplicationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/applications")
public class JobApplicationServlet extends HttpServlet {
    private JobApplicationService service;

    @Override
    public void init() {
        service = new JobApplicationService(new JobApplicationDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("applications", new ArrayList<JobApplication>());
        request.setAttribute("applicationCount", 0);
        String action = request.getParameter("action");
        try {
            if ("edit".equals(action)) {
                JobApplication application = service.findById(request.getParameter("id")).orElse(null);
                if (application == null) {
                    request.setAttribute("errorMessage", "That application no longer exists.");
                } else {
                    request.setAttribute("jobApplication", application);
                    request.setAttribute("editing", true);
                }
            }
            populateApplications(request, true);
        } catch (IllegalArgumentException exception) {
            request.setAttribute("errorMessage", exception.getMessage());
            try {
                populateApplications(request, false);
                request.setAttribute("search", request.getParameter("search") == null
                        ? "" : request.getParameter("search"));
            } catch (SQLException databaseException) {
                showDatabaseError("load applications", databaseException, request);
            }
            request.setAttribute("selectedStatus", "");
        } catch (SQLException exception) {
            showDatabaseError("load applications", exception, request);
        }
        request.setAttribute("today", java.time.LocalDate.now());
        request.setAttribute("statuses", JobApplicationService.getStatuses());
        request.getRequestDispatcher("/WEB-INF/views/applications.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        request.setAttribute("applications", new ArrayList<JobApplication>());
        request.setAttribute("applicationCount", 0);
        String action = request.getParameter("action");
        request.setAttribute("formSubmission", true);
        if ("update".equals(action)) {
            request.setAttribute("editing", true);
        }
        try {
            switch (action == null ? "" : action) {
                case "create" -> service.create(readApplication(request));
                case "update" -> {
                    JobApplication application = readApplication(request);
                    application.setId(parseId(request.getParameter("id")));
                    if (!service.update(application)) {
                        throw new IllegalArgumentException("That application no longer exists.");
                    }
                }
                case "delete" -> {
                    if (!service.delete(request.getParameter("id"))) {
                        throw new IllegalArgumentException("That application no longer exists.");
                    }
                }
                default -> throw new IllegalArgumentException("Choose a valid action.");
            }
            request.getSession().setAttribute("successMessage", successMessage(action));
            response.sendRedirect(request.getContextPath() + "/applications");
            return;
        } catch (IllegalArgumentException exception) {
            request.setAttribute("errorMessage", exception.getMessage());
        } catch (SQLException exception) {
            showDatabaseError("save application", exception, request);
        }

        try {
            populateApplications(request, false);
        } catch (SQLException exception) {
            showDatabaseError("reload applications", exception, request);
        }
        request.setAttribute("today", java.time.LocalDate.now());
        request.setAttribute("statuses", JobApplicationService.getStatuses());
        request.getRequestDispatcher("/WEB-INF/views/applications.jsp").forward(request, response);
    }

    private JobApplication readApplication(HttpServletRequest request) {
        JobApplication application = new JobApplication();
        application.setCompanyName(request.getParameter("companyName"));
        application.setJobRole(request.getParameter("jobRole"));
        application.setAppliedDate(JobApplicationService.parseDate(
                request.getParameter("appliedDate"), "Applied date"));
        application.setStatus(request.getParameter("status"));
        application.setInterviewDate(JobApplicationService.parseDate(
                request.getParameter("interviewDate"), "Interview date"));
        application.setNotes(request.getParameter("notes"));
        return application;
    }

    private long parseId(String rawId) {
        try {
            long id = Long.parseLong(rawId);
            if (id <= 0) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException | NullPointerException exception) {
            throw new IllegalArgumentException("Enter a valid application ID.");
        }
    }

    private void populateApplications(HttpServletRequest request, boolean useFilters) throws SQLException {
        String search = useFilters ? request.getParameter("search") : "";
        String status = useFilters ? request.getParameter("status") : "";
        List<JobApplication> applications = service.findAll(search, status);
        request.setAttribute("applications", applications);
        request.setAttribute("applicationCount", applications.size());
        request.setAttribute("search", search == null ? "" : search);
        request.setAttribute("selectedStatus", status == null ? "" : status);
    }

    private void showDatabaseError(String operation, SQLException exception, HttpServletRequest request) {
        getServletContext().log("Database error while attempting to " + operation + ".", exception);
        request.setAttribute("errorMessage",
                "The application could not access the database. Check the database setup and server logs.");
    }

    private String successMessage(String action) {
        return switch (action) {
            case "create" -> "Application added successfully.";
            case "update" -> "Application updated successfully.";
            case "delete" -> "Application deleted successfully.";
            default -> "Changes saved successfully.";
        };
    }
}
