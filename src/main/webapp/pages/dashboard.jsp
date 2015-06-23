<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Welcome ${userName}</title>
        <link href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet" type="text/css" />
    </head>
    <body>
        <jsp:include page="layout/header.jsp" />
			<jsp:include page="layout/nav.jsp" />
			<div id="section">
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
		        <h3>User profile</h3>
		        <ul>
		            <li><a href="${pageContext.request.contextPath}/pages/update_toi.jsp">Topics of Preference</a></li>
		            <li><a href="${pageContext.request.contextPath}/pages/update_coi.jsp">Conflicts of Interest</a></li>
		        </ul>
			</div>
			<jsp:include page="layout/footer.jsp" />
    </body>
</html>