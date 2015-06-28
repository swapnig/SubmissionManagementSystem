<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>${memberName} details</title>
		<script src="https://code.jquery.com/jquery-1.10.2.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/resources/js/ajaxRequest.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/resources/js/viewRegistrableMember.js" type="text/javascript"></script>
		<link href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
		<div id="container">
			<jsp:include page="../layout/header.jsp" />
			<jsp:include page="../layout/nav.jsp" />
			<div id="section">
				<form id="memberForm" action="" method="GET">
					<fieldset id="memberFormFieldSet" class="fieldSet">
						<legend id="memberLegend"><strong>${memberName} details</strong></legend>  
					    <table id="memberFields">
					    	<c:forEach items="${memberAttributes}" var="category" varStatus="loop">
					    	<tr>
								<td><label for="${category.name}">${category.name}</label></td>
								<c:choose>
									<c:when test="${loop.index == 0}">
								    	<td><input name="${category.name}" type="text" value="${category.value}" disabled/></td>
								  	</c:when>
								  	<c:otherwise>
								    	<td><textarea name="${category.name}" disabled>"${category.value}"</textarea></td>
								  	</c:otherwise>
								</c:choose>
							</tr>
						    </c:forEach>
						</table>
						<c:if test="${conductor == 'true'}">
							<input id="editMemberAttributes" type="button" value="Edit"/>
							<input type="submit" id="saveMemberAttributes" value="Update" style='display:none'/>
						</c:if>
						<input id="viewSubmittables" type="button" value="View Submittables"/>
						<input type="hidden" id="memberID" name="memberId" value="${memberId}" style='display:none'/>
					</fieldset>
				</form>
				<br/>
				<jsp:include page="../templates/registerOtherForMemberForm.jsp"/><br/>
				<fieldset id="submittablesFieldSet" class="fieldSet" style="display:none">
					<legend id="submittablesLegend"><strong>${memberName} submittables</strong></legend>
					<div id="submittables"></div>
				</fieldset>
				<div id="result"></div>
				
		        <br/>
		        <br/>
		        <div><a href="${pageContext.request.contextPath}/pages/status.jsp">View Grades for submissions</a></div>
			</div>
			<jsp:include page="../layout/footer.jsp" />
		</div>
		
	</body>
</html>