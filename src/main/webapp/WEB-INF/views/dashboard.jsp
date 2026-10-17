<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Dashboard | Job Application Tracker</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header class="site-header">
    <a class="brand" href="${pageContext.request.contextPath}/dashboard">Track<span>MyJob</span></a>
    <nav aria-label="Main navigation">
        <a class="nav-link active" href="${pageContext.request.contextPath}/dashboard">Dashboard</a>
        <a class="nav-link" href="${pageContext.request.contextPath}/applications">Applications</a>
    </nav>
</header>
<main class="page-shell">
    <section class="page-heading">
        <div>
            <p class="eyebrow">PLACEMENT SEASON, ORGANIZED</p>
            <h1>Your job search at a glance</h1>
            <p class="muted">Keep every opportunity moving forward, all in one place.</p>
        </div>
        <a class="button button-primary" href="${pageContext.request.contextPath}/applications">＋ Add application</a>
    </section>

    <c:if test="${not empty errorMessage}">
        <div class="notice notice-error" role="alert"><c:out value="${errorMessage}"/></div>
    </c:if>

    <section class="stats-grid" aria-label="Application statistics">
        <article class="stat-card stat-total">
            <span class="stat-label">Total applications</span>
            <strong class="stat-number"><c:out value="${totalApplications}"/></strong>
            <span class="stat-footnote">Opportunities you’re tracking</span>
        </article>
        <c:forEach var="entry" items="${statusCounts}">
            <article class="stat-card">
                <span class="stat-label"><c:out value="${entry.key}"/></span>
                <strong class="stat-number"><c:out value="${entry.value}"/></strong>
                <a class="stat-link" href="${pageContext.request.contextPath}/applications?status=${entry.key}">View applications <span aria-hidden="true">↗</span></a>
            </article>
        </c:forEach>
    </section>

    <section class="tip-card">
        <div class="tip-icon" aria-hidden="true">✦</div>
        <div>
            <h2>A little progress adds up</h2>
            <p class="muted">Log an application as soon as you apply, then update its status as you hear back.</p>
        </div>
        <a class="text-link" href="${pageContext.request.contextPath}/applications">Manage applications <span aria-hidden="true">→</span></a>
    </section>
</main>
<footer class="site-footer">Built for your next opportunity.</footer>
</body>
</html>
