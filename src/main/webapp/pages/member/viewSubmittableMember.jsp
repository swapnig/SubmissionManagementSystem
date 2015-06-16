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
		<script src="${pageContext.request.contextPath}/resources/js/viewSubmittableMember.js" type="text/javascript"></script>
		<link href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
		<div class="container">
		<jsp:include page="templates/header.jsp" />
		    <div class="left">
		        <c:if test="${CONDUCTOR == 'true'}">
					<fieldset id="conductorFieldSet" class="smallFieldSet">
						<legend id="conductorLegend"><strong>Conductor links</strong></legend>
					</fieldset>
				</c:if>
				<c:if test="${EVALUATOR == 'true'}">
					<fieldset id="evaluatorFieldSet" class="smallFieldSet">
						<legend id="evaluatorLegend"><strong>Evaluator links</strong></legend>
					</fieldset>
				</c:if>
		    </div>
		    <div class="right">
		        <form id="memberForm" action="" method="GET">
					<fieldset id="memberFormFieldSet" class="normalFieldSet">
						<legend id="memberLegend"><strong>${memberName} details</strong></legend>  
					    <table id="memberFields">
					    	<c:forEach items="${memberAttributes}" var="category">
					    	<tr>
								<td><label for="${category.name}">${category.name}</label></td>
								<td><input name="${category.name}" type="text" value="${category.value}" disabled/></td>
							</tr>
						    </c:forEach>
						</table>
						<c:if test="${CONDUCTOR == 'true'}">
							<input id="editMemberAttributes" type="button" value="Edit"/>
							<input type="submit" id="saveMemberAttributes" value="Update" style='display:none'/>
						</c:if>
						
						<input type="hidden" id="memberID" name="memberId" value="${memberId}" style='display:none'/>
					</fieldset>
				</form>
				<br/>
				<jsp:include page="templates/registerOtherForMemberForm.jsp"/><br/>
				
				<c:if test="${SUBMITTER == 'true'}">
					<fieldset id="reviewerFieldSet" class="normalFieldSet">
						<legend id="reviewerLegend"><strong>Submitter links</strong></legend>
					</fieldset>
				</c:if>
				
				<div id="result"></div>
		    </div>
		</div>
	</body>
</html>