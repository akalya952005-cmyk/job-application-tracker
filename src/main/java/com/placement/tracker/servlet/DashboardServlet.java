package com.placement.tracker.servlet;

import com.placement.tracker.dao.JobApplicationDAO;
import com.placement.tracker.service.JobApplicationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private JobApplicationService service;

    @Override
    public void init() {
        service = new JobApplicationService(new JobApplicationDAO());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, Long> counts = new LinkedHashMap<>();
        request.setAttribute("totalApplications", 0L);
        try {
            Map<String, Long> actualCounts = service.getStatusCounts();
            for (String status : JobApplicationService.getStatuses()) {
                counts.put(status, actualCounts.getOrDefault(status, 0L));
            }
            long total = counts.values().stream().mapToLong(Long::longValue).sum();
            request.setAttribute("totalApplications", total);
        } catch (SQLException exception) {
            getServletContext().log("Database error while loading dashboard statistics.", exception);
            request.setAttribute("errorMessage",
                    "Dashboard statistics are unavailable. Check the database setup and server logs.");
        }
        request.setAttribute("statusCounts", counts);
        request.getRequestDispatcher("/WEB-INF/views/dashboard.jsp").forward(request, response);
    }
}
