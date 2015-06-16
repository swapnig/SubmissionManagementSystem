<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Insert title here</title>
		<script src="https://code.jquery.com/jquery-1.10.2.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/resources/js/ajaxRequest.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/resources/js/viewRegistrableMember.js" type="text/javascript"></script>
	</head>
	<body>
		<jsp:include page="templates/header.jsp" />
		<form id="memberForm" action="" method="GET">
			<fieldset id="memberFormFieldSet" style="width:400px">
				<legend id="memberLegend"><strong>${memberName} details</strong></legend>  
			    <table id="memberFields">
			    	<c:forEach items="${memberAttributes}" var="category">
			    	<tr>
						<td><label for="${category.name}">${category.name}</label></td>
						<td><input name="${category.name}" type="text" value="${category.value}" disabled/></td>
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
		<jsp:include page="templates/registerOtherForMemberForm.jsp"/><br/>
		<fieldset id="submittablesFieldSet" style="width:400px; display:none">
			<legend id="submittablesLegend"><strong>${memberName} submittables</strong></legend>
			<div id="submittables"></div>
		</fieldset>
		<div id="result"></div>
	</body>
</html>