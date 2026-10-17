package com.placement.tracker.service;

import com.placement.tracker.dao.JobApplicationRepository;
import com.placement.tracker.model.JobApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JobApplicationServiceTest {
    private InMemoryRepository repository;
    private JobApplicationService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository();
        service = new JobApplicationService(repository);
    }

    @Test
    void createTrimsValuesAndDefaultsStatusToApplied() throws SQLException {
        JobApplication application = application();
        application.setCompanyName("  Acme  ");
        application.setStatus(" ");

        service.create(application);

        assertEquals("Acme", repository.created.getCompanyName());
        assertEquals("Applied", repository.created.getStatus());
    }

    @Test
    void createRejectsMissingCompanyName() {
        JobApplication application = application();
        application.setCompanyName("  ");

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> service.create(application));

        assertEquals("Company name is required.", error.getMessage());
        assertNull(repository.created);
    }

    @Test
    void createRejectsMissingJobRole() {
        JobApplication application = application();
        application.setJobRole(null);

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class, () -> service.create(application));

        assertEquals("Job role is required.", error.getMessage());
    }

    @Test
    void createRejectsUnknownStatus() {
        JobApplication application = application();
        application.setStatus("Pending");

        assertThrows(IllegalArgumentException.class, () -> service.create(application));
    }

    @Test
    void createRejectsFutureAppliedDate() {
        JobApplication application = application();
        application.setAppliedDate(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> service.create(application));
    }

    @Test
    void createRejectsInterviewBeforeAppliedDate() {
        JobApplication application = application();
        application.setInterviewDate(application.getAppliedDate().minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> service.create(application));
    }

    @Test
    void parseDateRejectsMalformedDate() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> JobApplicationService.parseDate("not-a-date", "Applied date"));

        assertEquals("Applied date must be a valid date.", error.getMessage());
    }

    @Test
    void parseDateAllowsOptionalBlankDate() {
        assertNull(JobApplicationService.parseDate(" ", "Interview date"));
    }

    @Test
    void findByIdRejectsInvalidIdsWithoutQueryingRepository() {
        assertThrows(IllegalArgumentException.class, () -> service.findById("0"));
        assertThrows(IllegalArgumentException.class, () -> service.findById("invalid"));
        assertEquals(0, repository.lookupCount);
    }

    @Test
    void deleteReturnsFalseWhenApplicationDoesNotExist() throws SQLException {
        assertFalse(service.delete("15"));
    }

    @Test
    void findAllRejectsUnknownFilterStatus() {
        assertThrows(IllegalArgumentException.class, () -> service.findAll("", "pending"));
    }

    @Test
    void findAllPassesTrimmedSearchAndStatusToRepository() throws SQLException {
        service.findAll("  acme ", " Interview ");

        assertEquals("acme", repository.lastSearch);
        assertEquals("Interview", repository.lastStatus);
    }

    private JobApplication application() {
        return new JobApplication(0, "Acme", "Engineer", LocalDate.now(), "", null, "Notes");
    }

    private static class InMemoryRepository implements JobApplicationRepository {
        private JobApplication created;
        private int lookupCount;
        private String lastSearch;
        private String lastStatus;

        @Override
        public List<JobApplication> findAll(String search, String status) {
            lastSearch = search;
            lastStatus = status;
            return List.of();
        }

        @Override
        public Optional<JobApplication> findById(long id) {
            lookupCount++;
            return Optional.empty();
        }

        @Override
        public long create(JobApplication application) {
            created = application;
            return 1;
        }

        @Override
        public boolean update(JobApplication application) {
            return false;
        }

        @Override
        public boolean delete(long id) {
            return false;
        }

        @Override
        public Map<String, Long> getStatusCounts() {
            return Map.of();
        }
    }
}
