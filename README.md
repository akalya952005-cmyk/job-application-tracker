# Job Application Tracker

A beginner-friendly Java web application for managing placement and job applications. It uses Java 25, Maven, JDBC, MySQL, Jakarta Servlets/JSP, and Apache Tomcat 10.1. The browser interface lets you maintain applications and review live status counts from the database.

## Features

- Add, list, search, filter, edit, and delete job applications.
- Dashboard totals calculated from saved database records.
- Server-side validation for required values, valid statuses, IDs, and dates.
- Parameterized SQL, escaped JSP output, POST for changes, and redirect-after-success.
- Responsive dashboard and application table.
- JUnit 5 business-logic tests that run without a MySQL server.

## Technology

- Java 25 and Maven WAR packaging.
- Jakarta Servlet 6.0 / JSP on Tomcat 10.1.
- MySQL Community Server and MySQL Connector/J.
- Jakarta Standard Tag Library (JSTL) for JSP rendering.
- JUnit 5 for unit tests.

## Project layout

```text
.
├── .env.example
├── .gitignore
├── README.md
├── pom.xml
├── database/
│   └── schema.sql
└── src/
    ├── main/
    │   ├── java/com/placement/tracker/
    │   │   ├── dao/       # JDBC repository and SQL
    │   │   ├── model/     # JobApplication
    │   │   ├── service/   # Validation and business rules
    │   │   ├── servlet/   # HTTP request controllers
    │   │   └── util/      # Environment-based DB connections
    │   └── webapp/
    │       ├── css/
    │       ├── WEB-INF/
    │       │   ├── views/ # JSP views, protected from direct requests
    │       │   └── web.xml
    │       └── index.jsp
    └── test/java/com/placement/tracker/service/
        └── JobApplicationServiceTest.java
```

## Prerequisites

Install these on Windows before running the application. MySQL Workbench and VS Code are already present.

1. **JDK 25 (LTS):** Download and install a JDK 25 distribution, such as [Eclipse Temurin 25](https://adoptium.net/temurin/releases/?version=25). During setup, select the options to set `JAVA_HOME` and update `PATH`, if offered.
2. **VS Code Java extensions:** In VS Code, open Extensions (`Ctrl+Shift+X`) and install **Extension Pack for Java** by Microsoft. This is an editor convenience; Maven builds also work from a terminal without it.
3. **Apache Maven:** Download the Binary zip archive from [maven.apache.org](https://maven.apache.org/download.cgi), extract it (for example, to `C:\tools\apache-maven`), and add its `bin` folder to the Windows `Path`.
4. **MySQL Server and Workbench:** Install MySQL Community Server and MySQL Workbench using the [official MySQL Windows Installer](https://dev.mysql.com/downloads/installer/). Select the server and Workbench products in the installer. Workbench alone is a database client and does not provide a running MySQL server.
5. **Apache Tomcat:** Download the **Tomcat 10.1 Windows Service Installer** or ZIP distribution from [tomcat.apache.org](https://tomcat.apache.org/download-10.cgi). The ZIP option can be extracted, for example, to `C:\tools\apache-tomcat-10.1`. Confirm that Tomcat runs on Java 25. Do not install Tomcat 9: it uses the older `javax.servlet` API.

Open **Settings → System → About → Advanced system settings → Environment Variables** to set `JAVA_HOME` to the JDK 25 installation directory (for example, `C:\Program Files\Eclipse Adoptium\jdk-25...`) and add `%JAVA_HOME%\bin` and Maven's `bin` directory to `Path`. Open a new terminal after changing `Path`. Verify:

```powershell
java -version
javac -version
mvn -version
```

Both Java commands should report version 25; Maven should report its version and Java 25. If `winget` is available, you can search for package IDs (`winget search temurin`, `winget search maven`, or `winget search mysql`) and choose an official matching package. The manual downloads above are the fallback if a package is not listed.

## Open the project in VS Code

1. Open VS Code and choose **File → Open Folder**.
2. Select the project folder containing this README and `pom.xml`.
3. If VS Code asks, trust the folder and let the Java extensions import the Maven project.
4. Open **Terminal → New Terminal**. The terminal should start in the project root, where `pom.xml` is located.

## Database setup

1. Start the MySQL Server service from Windows Services or MySQL Installer.
2. Open MySQL Workbench and connect to your local server as an administrative user.
3. Open `database/schema.sql` in Workbench and execute it. It creates the `placement_tracker` database and `job_applications` table, then inserts three fictional example rows (only if the example records are not already there).
4. In Workbench, create a separate local application account. Replace the placeholder with a strong password of your choice; do not commit a real password:

```sql
CREATE USER IF NOT EXISTS 'tracker_user'@'localhost' IDENTIFIED BY 'REPLACE_WITH_A_LOCAL_PASSWORD';
GRANT SELECT, INSERT, UPDATE, DELETE ON placement_tracker.* TO 'tracker_user'@'localhost';
```

The application reads `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` from the process environment. Set these for the PowerShell session used to start Tomcat. The password prompt below is masked and the password is not written to a project file:

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3306/placement_tracker?serverTimezone=UTC'
$env:DB_USERNAME = 'tracker_user'
$securePassword = Read-Host 'MySQL application-user password' -AsSecureString
$env:DB_PASSWORD = [System.Net.NetworkCredential]::new('', $securePassword).Password
```

These session variables are not saved to Git and disappear when that terminal closes. If you run Tomcat as a Windows service, configure the same variables in the service's environment or launch configuration instead. `.env.example` is a reference only; Java does not automatically load `.env` files. Keep actual secrets out of source control.

## Build and test

From the project directory in the VS Code integrated terminal:

```powershell
mvn clean test
mvn package
```

`mvn clean test` compiles the app and runs the JUnit 5 validation/business-logic tests. `mvn package` creates `target\job-application-tracker.war`. Check that this file exists before deploying. The test suite uses an in-memory test repository; it does **not** connect to MySQL. See [Database verification](#database-verification) for manual integration checks.

## Deploy to Tomcat 10.1

1. Confirm Tomcat's HTTP connector port in `<Tomcat installation>\conf\server.xml`: find the HTTP `<Connector>` and note its `port` value. The standard Tomcat 10.1 configuration uses port `8080`; if yours differs, use your configured port below.
2. Build the WAR with `mvn package`.
3. Keep the PowerShell terminal with the database environment variables set open, then start Tomcat from that same terminal:

```powershell
& 'C:\tools\apache-tomcat-10.1\bin\startup.bat'
```

Adjust the path to match the folder where you installed Tomcat. If Tomcat is installed as a Windows service, start it from Windows Services and configure environment variables for that service.

4. Copy or deploy the WAR to Tomcat's `webapps` directory:

```powershell
Copy-Item '.\target\job-application-tracker.war' 'C:\tools\apache-tomcat-10.1\webapps\'
```

Use the actual Tomcat `webapps` path if it differs. Tomcat expands the WAR automatically. Check Tomcat's `logs` folder if deployment reports an error.
5. In a browser, open `http://localhost:<HTTP connector port>/job-application-tracker/` (for the unmodified default connector, `http://localhost:8080/job-application-tracker/`). Tomcat will redirect the root application path to the dashboard.

To stop a ZIP-installed Tomcat instance, use `& 'C:\tools\apache-tomcat-10.1\bin\shutdown.bat'`.

## Database verification

After starting MySQL, creating the schema/account, setting the environment variables, and deploying the WAR, verify the following in the browser:

1. **Add:** Submit a company, job role, applied date, and status. The new record should appear and a success message should be shown.
2. **View:** Open Applications and check that the record and its dates/notes appear.
3. **Update:** Choose Edit, change a field/status, and save; confirm the displayed row changes.
4. **Delete:** Choose Delete and confirm the browser prompt; verify the row disappears.
5. **Search:** Search for a distinctive company name or role. Search uses a parameterized `LIKE` query.
6. **Filter:** Choose a status and submit; only matching records should appear.
7. **Dashboard:** Compare the status cards with the grouped counts from this query:

```sql
SELECT status, COUNT(*) AS application_count
FROM job_applications
GROUP BY status;
```

Also use the application's form to check missing required fields, malformed dates (server-side validation), and interview dates preceding application dates. Invalid IDs can be checked by visiting `/applications?action=edit&id=0` or by posting a non-numeric ID using a test client; the application should report a useful error rather than acting on a record. To exercise a DB connection failure, stop the MySQL service or temporarily use an invalid `DB_URL`, restart Tomcat, and check the user-facing error and Tomcat log. Restore the correct service/configuration afterward. These integration checks require a working local MySQL server and Tomcat; they have not been run automatically by the unit tests.

## Example SQL queries

```sql
USE placement_tracker;

SELECT id, company_name, job_role, applied_date, status, interview_date
FROM job_applications
ORDER BY applied_date DESC;

SELECT company_name, job_role, status
FROM job_applications
WHERE company_name LIKE '%systems%' OR job_role LIKE '%engineer%';

SELECT status, COUNT(*) AS application_count
FROM job_applications
GROUP BY status;
```

## Common problems

- **`java` or `mvn` is not recognized:** Install JDK 25/Maven, check `JAVA_HOME` and `Path`, then open a fresh terminal.
- **Tomcat fails to start:** Check for another service using the configured port and inspect Tomcat's `logs`. Verify Tomcat is running with Java 25.
- **404 at the app URL:** Confirm that `job-application-tracker.war` is in the correct Tomcat `webapps` folder, that Tomcat expanded it, and that the URL context matches the WAR filename.
- **Database unavailable message:** Start MySQL, execute `database/schema.sql`, create the app user, set all three environment variables in the environment inherited by Tomcat, and restart Tomcat.
- **Access denied:** Verify `DB_USERNAME`, `DB_PASSWORD`, the MySQL account host (`localhost`), and that the account has privileges on `placement_tracker`.
- **JSP tag library errors:** Make sure Tomcat completed deployment and inspect its logs. JSTL API and implementation are included as Maven dependencies in the WAR.
- **Port differs:** Use the HTTP connector's actual `port` in `conf/server.xml`; do not assume the default if it has been changed.

## Publish to GitHub

1. Check `git status` and confirm you have not added a real password or private information. `.gitignore` excludes build products, `.env`, and IDE files.
2. Create an empty repository on GitHub. Do not add a generated license or README if you want to push this existing project as-is.
3. In the project directory, initialize and publish:

```powershell
git init
git add .
git status
git commit -m "Build job application tracker"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/YOUR-REPOSITORY.git
git push -u origin main
```

Replace the remote URL with your own repository URL. Review the staged files before committing.

Suggested real screenshot checklist (capture your own running app; none are included): dashboard with actual sample counts; add/edit form; application table with representative records; filtered/search results; mobile-width responsive layout.

## Test coverage notes

JUnit tests cover status defaults, input trimming, required fields, allowed statuses, date validation, malformed date parsing, invalid IDs, and search/filter argument validation. JDBC CRUD, SQL parameter binding, MySQL connection errors, and dashboard aggregation should be verified with the manual integration checks above; no MySQL integration tests are claimed as passing.
