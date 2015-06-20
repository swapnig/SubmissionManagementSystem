<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<jsp:include page="member/templates/header.jsp" />
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Welcome ${userName} - SMS</title>
    </head>
    <body>
        <h1>Welcome to Submission Management System</h1>
        
        <c:if test="${empty rolesToMembers}">
            <font size="5" color="red">You are not registered with any member!! Use above links now!!</font>
            <br/>
        </c:if>
        <c:forEach var="roleToMembers" items="${rolesToMembers}">
            <c:if test="${not empty roleToMembers.value}">
                <h3>You are registered as ${roleToMembers.key} for following:</h3>
            </c:if>
            <ul>
                <c:forEach var="member" items="${roleToMembers.value}">
                    <li><a href="${pageContext.request.contextPath}/ViewRegistrableMember?memberId=${member.getId()}">${member.getName()}</a></li>
                </c:forEach>
            </ul>
        </c:forEach>
        <div id="StudentTableContainer"></div>

        <br/>
        <h3>Update user profile</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/pages/update_toi.jsp">Update Topics of Preference</a></li>
            <li><a href="${pageContext.request.contextPath}/pages/update_coi.jsp">Update Conflicts of Interest</a></li>
        </ul>
        <hr/>
    </body>
</html>