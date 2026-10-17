CREATE DATABASE IF NOT EXISTS placement_tracker
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE placement_tracker;

CREATE TABLE IF NOT EXISTS job_applications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    company_name VARCHAR(150) NOT NULL,
    job_role VARCHAR(150) NOT NULL,
    applied_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'Applied',
    interview_date DATE NULL,
    notes TEXT NULL,
    PRIMARY KEY (id),
    CONSTRAINT chk_job_applications_status CHECK (
        status IN ('Applied', 'Online Assessment', 'Interview', 'Offered', 'Rejected', 'Withdrawn')
    )
);

INSERT INTO job_applications
    (company_name, job_role, applied_date, status, interview_date, notes)
SELECT 'Northstar Analytics', 'Junior Data Engineer', '2026-08-12', 'Online Assessment', NULL,
       'Fictional sample record. Assessment invitation received.'
WHERE NOT EXISTS (
    SELECT 1 FROM job_applications
    WHERE company_name = 'Northstar Analytics' AND job_role = 'Junior Data Engineer'
);

INSERT INTO job_applications
    (company_name, job_role, applied_date, status, interview_date, notes)
SELECT 'Bluepeak Systems', 'Graduate Software Developer', '2026-08-18', 'Interview', '2026-09-02',
       'Fictional sample record. Technical interview scheduled.'
WHERE NOT EXISTS (
    SELECT 1 FROM job_applications
    WHERE company_name = 'Bluepeak Systems' AND job_role = 'Graduate Software Developer'
);

INSERT INTO job_applications
    (company_name, job_role, applied_date, status, interview_date, notes)
SELECT 'Cedarwave Technologies', 'Associate Engineer', '2026-08-24', 'Applied', NULL,
       'Fictional sample record.'
WHERE NOT EXISTS (
    SELECT 1 FROM job_applications
    WHERE company_name = 'Cedarwave Technologies' AND job_role = 'Associate Engineer'
);
