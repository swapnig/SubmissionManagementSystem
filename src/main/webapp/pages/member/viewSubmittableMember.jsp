<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1" 
	import="edu.neu.ccis.sms.constants.SessionKeys"%>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    // Get the current user id
    Long userId = (Long) session.getAttribute(SessionKeys.keyUserId);

    // Load all the submittable Member Details
    Long activeMemberId = (Long) session.getAttribute(SessionKeys.activeMemberId);

    Long submittableMemberId = (Long) session.getAttribute(SessionKeys.activeSubmittableMemberId);
%>
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
		<div id="container">
			<jsp:include page="../layout/header.jsp" />
			<jsp:include page="../layout/nav.jsp" />
			<div id="section">
				<div class="left">
			        <c:if test="${CONDUCTOR == 'true'}">
						<fieldset id="conductorFieldSet" class="leftFieldSet">
							<legend id="conductorLegend"><strong>Conductor links</strong></legend>
							<!-- Add links to pages which should be accessible to Evaluators role only -->
							<ul>
	                            <li><a href="${pageContext.request.contextPath}/pages/allocate_to_evaluators.jsp">Allocate Evaluators (for grading submissions)</a></li>
	                            <li><a href="${pageContext.request.contextPath}/pages/disseminate_evaluations.jsp">Disseminate Evaluations (to students)</a></li>
	                        </ul>
						</fieldset>
					</c:if>
					<c:if test="${EVALUATOR == 'true'}">
						<fieldset id="evaluatorFieldSet" class="leftFieldSet">
							<legend id="evaluatorLegend"><strong>Evaluator links</strong></legend>
							<!-- Add links to pages which should be accessible to Evaluators role only -->
							<ul>
	                            <li><a href="${pageContext.request.contextPath}/pages/document_retrieval.jsp">Download submissions for grading</a></li>
	                            <li><a href="${pageContext.request.contextPath}/pages/upload_evaluations.jsp">Upload Evaluations</a></li>
	                        </ul>
						</fieldset>
					</c:if>
					<c:if test="${SUBMITTER == 'true'}">
						<fieldset id="reviewerFieldSet" class="leftFieldSet">
							<legend id="reviewerLegend"><strong>Submitter links</strong></legend>
							<ul>
	                            <li><a href="${pageContext.request.contextPath}/pages/submit_to_member.jsp">Upload Submission</a></li>
	                        </ul>
						</fieldset>
					</c:if>
			    </div>
			    <div class="right">
			        <form id="memberForm" action="" method="GET">
						<fieldset id="memberFormFieldSet" class="rightFieldSet">
							<legend id="memberLegend"><strong>${memberName} details</strong></legend>  
						    <table id="memberFields">
						    	<c:forEach items="${memberAttributes}" var="category" varStatus="loop">
						    	<tr>
									<td style="width:1px"><label for="${category.name}">${category.name}</label></td>
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
							<c:if test="${CONDUCTOR == 'true'}">
								<input id="editMemberAttributes" type="button" value="Edit"/>
								<input type="submit" id="saveMemberAttributes" value="Update" style='display:none'/>
							</c:if>
							
							<input type="hidden" id="memberID" name="memberId" value="${memberId}" style='display:none'/>
						</fieldset>
					</form>
					<br/>
					<div id="result"></div>
			    </div>
			</div>
			<jsp:include page="../layout/footer.jsp" />
		</div>
	</body>
</html>