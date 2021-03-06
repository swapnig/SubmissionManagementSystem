<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib  uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Register for Member</title>
		<script src="https://code.jquery.com/jquery-1.10.2.js" type="text/javascript"></script>
		<script type="text/javascript">
			// Convert server side hashmap using jstl to javascript map
			// Was having a issue where javascript was interpreting hashmap as a flattened list
		 	var categoryToParentMap = { };
			<c:forEach var="category" items="${categoryToParent}">
				categoryToParentMap['${category.key}'] = '${category.value}';
			</c:forEach>
		</script>
		<script src="${pageContext.request.contextPath}/resources/js/ajaxRequest.js" type="text/javascript"></script>
		<script src="${pageContext.request.contextPath}/resources/js/registerForMember.js" type="text/javascript"></script>
		<link href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet" type="text/css" />
	</head>
	<body>
		<div id="container">
			<jsp:include page="../layout/header.jsp" />
			<jsp:include page="../layout/nav.jsp" />
			<div id="section">
				<h2 style="margin:0px">Register for member</h2><br>
				<jsp:include page="../templates/selectCategoryForRegistration.jsp" /><br/>
				<jsp:include page="../templates/selectExistingMembers.jsp" /><br/>
				<jsp:include page="../templates/memberForm.jsp" /><br/>
				<jsp:include page="../templates/registerSelfForMemberForm.jsp" /><br/>
				<div id="result"></div>
			</div>
			<jsp:include page="../layout/footer.jsp" />
		</div>
	</body>
</html>