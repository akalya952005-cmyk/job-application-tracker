<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Applications | Job Application Tracker</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header class="site-header">
    <a class="brand" href="${pageContext.request.contextPath}/dashboard">Track<span>MyJob</span></a>
    <nav aria-label="Main navigation">
        <a class="nav-link" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
        <a class="nav-link active" href="${pageContext.request.contextPath}/applications">Applications</a>
    </nav>
</header>
<main class="page-shell">
    <section class="page-heading">
        <div>
            <p class="eyebrow">YOUR OPPORTUNITIES</p>
            <h1>Applications</h1>
            <p class="muted">One clear view of every role you’re pursuing.</p>
        </div>
        <a class="button button-primary" href="#application-form">＋ Add application</a>
    </section>

    <c:if test="${not empty successMessage}">
        <div class="notice notice-success" role="status"><c:out value="${successMessage}"/></div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="notice notice-error" role="alert"><c:out value="${errorMessage}"/></div>
    </c:if>

    <section class="panel form-panel" id="application-form">
        <div class="panel-heading">
            <div>
                <p class="eyebrow"><c:choose><c:when test="${editing}">EDIT DETAILS</c:when><c:otherwise>NEW OPPORTUNITY</c:otherwise></c:choose></p>
                <h2><c:choose><c:when test="${editing}">Update application</c:when><c:otherwise>Add an application</c:otherwise></c:choose></h2>
            </div>
        </div>
        <form method="post" action="${pageContext.request.contextPath}/applications" class="application-form">
            <input type="hidden" name="action" value="${editing ? 'update' : 'create'}">
            <c:if test="${editing}">
                <input type="hidden" name="id" value="<c:out value='${formSubmission ? param.id : jobApplication.id}'/>">
            </c:if>
            <div class="form-grid">
                <label>Company name <span class="required">*</span>
                    <input type="text" name="companyName" maxlength="150" required value="<c:out value='${formSubmission ? param.companyName : jobApplication.companyName}'/>" placeholder="e.g. Acme Technologies">
                </label>
                <label>Job role <span class="required">*</span>
                    <input type="text" name="jobRole" maxlength="150" required value="<c:out value='${formSubmission ? param.jobRole : jobApplication.jobRole}'/>" placeholder="e.g. Graduate Software Engineer">
                </label>
                <label>Applied date <span class="required">*</span>
                    <input type="date" name="appliedDate" required max="${today}" value="<c:out value='${formSubmission ? param.appliedDate : jobApplication.appliedDate}'/>">
                </label>
                <label>Application status
                    <select name="status">
                        <c:forEach var="status" items="${statuses}">
                            <option value="<c:out value='${status}'/>" <c:if test="${(formSubmission and param.status eq status) or (not formSubmission and not empty jobApplication and jobApplication.status eq status) or (not formSubmission and empty jobApplication and status eq 'Applied')}">selected</c:if>><c:out value="${status}"/></option>
                        </c:forEach>
                    </select>
                </label>
                <label>Interview date
                    <input type="date" name="interviewDate" value="<c:out value='${formSubmission ? param.interviewDate : jobApplication.interviewDate}'/>">
                </label>
                <label class="full-width">Notes
                    <textarea name="notes" rows="3" maxlength="5000" placeholder="Job description, contact details, or reminders"><c:out value="${formSubmission ? param.notes : jobApplication.notes}"/></textarea>
                </label>
            </div>
            <div class="form-actions">
                <button type="submit" class="button button-primary">
                    <c:choose>
                        <c:when test="${editing}">Save changes</c:when>
                        <c:otherwise>Save application</c:otherwise>
                    </c:choose>
                </button>
                <c:if test="${editing}">
                    <a class="button button-quiet" href="${pageContext.request.contextPath}/applications">Cancel edit</a>
                </c:if>
            </div>
        </form>
    </section>

    <section class="panel list-panel">
        <div class="panel-heading list-heading">
            <div>
                <p class="eyebrow">APPLICATION TRACKER</p>
                <h2>All applications <span class="count-pill"><c:out value="${applicationCount}"/></span></h2>
            </div>
        </div>
        <form method="get" action="${pageContext.request.contextPath}/applications" class="filter-form">
            <label class="search-field">
                <span class="sr-only">Search by company or job role</span>
                <span aria-hidden="true">⌕</span>
                <input type="search" name="search" value="<c:out value='${search}'/>" placeholder="Search company or role">
            </label>
            <label class="filter-select">
                <span class="sr-only">Filter by status</span>
                <select name="status">
                    <option value="">All statuses</option>
                    <c:forEach var="status" items="${statuses}">
                        <option value="<c:out value='${status}'/>" <c:if test="${selectedStatus eq status}">selected</c:if>><c:out value="${status}"/></option>
                    </c:forEach>
                </select>
            </label>
            <button type="submit" class="button button-secondary">Search</button>
            <a class="button button-quiet" href="${pageContext.request.contextPath}/applications">Clear</a>
        </form>

        <c:choose>
            <c:when test="${empty applications}">
                <div class="empty-state">
                    <div class="empty-icon" aria-hidden="true">⌕</div>
                    <h3>No applications found</h3>
                    <p class="muted">Try another search or status, or add your first opportunity above.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>Company &amp; role</th>
                            <th>Applied</th>
                            <th>Status</th>
                            <th>Interview</th>
                            <th>Notes</th>
                            <th><span class="sr-only">Actions</span></th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="item" items="${applications}">
                            <tr>
                                <td><strong><c:out value="${item.companyName}"/></strong><span class="table-subtitle"><c:out value="${item.jobRole}"/></span></td>
                                <td><c:out value="${item.appliedDate}"/></td>
                                <td><span class="status-pill"><c:out value="${item.status}"/></span></td>
                                <td><c:out value="${item.interviewDate}"/></td>
                                <td class="notes-cell"><c:out value="${item.notes}"/></td>
                                <td class="row-actions">
                                    <a class="icon-button" aria-label="Edit application" href="${pageContext.request.contextPath}/applications?action=edit&amp;id=${item.id}">Edit</a>
                                    <form method="post" action="${pageContext.request.contextPath}/applications" onsubmit="return confirm('Delete this application? This cannot be undone.');">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="<c:out value='${item.id}'/>">
                                        <button type="submit" class="icon-button danger-button">Delete</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>
<footer class="site-footer">Built for your next opportunity.</footer>
</body>
</html>
