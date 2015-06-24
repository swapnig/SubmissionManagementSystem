<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="edu.neu.ccis.sms.constants.SessionKeys,
    javax.servlet.http.HttpSession,java.util.*,
    javax.servlet.http.HttpServletRequest,
    edu.neu.ccis.sms.dao.categories.*,
    edu.neu.ccis.sms.dao.users.*,
    edu.neu.ccis.sms.entity.categories.*,
    edu.neu.ccis.sms.entity.users.*"%>
<jsp:include page="layout/header.jsp" />
<jsp:include page="layout/nav.jsp" />
<%
    // Check if there is any message to show in page
    String message = (String) request.getAttribute("message");

    // Get the current user id
    Long userId = (Long) session.getAttribute(SessionKeys.keyUserId);

    // Load all the submittable Member Details
    Long activeMemberId = (Long) session.getAttribute(SessionKeys.activeMemberId);

    Long submittableMemberId = (Long) session.getAttribute(SessionKeys.activeSubmittableMemberId);

    // DAOs
    UserDao userDao = new UserDaoImpl();
    MemberDao memDao = new MemberDaoImpl();
    UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();

    RoleType role = userToMemberMappingDao.getUsersRoleForMember(userId, activeMemberId);
    if(role == null || role == RoleType.SUBMITTER){
        response.sendRedirect("dashboard.jsp");
    }

    Member activeMember = memDao.getMember(activeMemberId);
    Member submittableMember = memDao.getMember(submittableMemberId);

    String activeMemberName = activeMember.getName();
    String submittableMemberName = submittableMember.getName();

    // Get the usersToEvaluate for this RoleType.EVALUATOR user
    User user = userDao.getUserByIdWithSubmittersToEvaluateMappings(userId);
    Set<User> submittersToEvaluate = user.getSubmittersToEvaluateForMemberId(submittableMemberId);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <link href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet" type="text/css"/>
        <title>Upload Evaluations for <%=activeMemberName%> - <%=submittableMemberName%></title>
    </head>
    <body>
        <div>Upload Evaluations for <%=activeMemberName%> - <%=submittableMemberName%></div>
        <hr />
        <form action="<%=request.getContextPath()%>/UploadEvaluations" method="POST">
            <label style="width:300px;display:inline-block;" for="maxGrades">Maximum Grades (Out of total)</label>
            <input type="text" id="maxGrades" name="maxGrades" value="100" size=30 maxlength=5>
            <input type="hidden" id="submittableMemberId" name="submittableMemberId" value="<%=submittableMemberId%>">
            <br/>
            <br/>
            <table cellpadding="3" border="2">
                <tr>
                    <th>Submitter Email-Id</th>
                    <th>Grades Received</th>
                    <th>Comments</th>
                </tr>
                <%-- Create individual rows dynamically for each submitter for which evaluation to be done --%>
                <%
                    int i = 0;
                    for (User submitter : submittersToEvaluate) {
                        out.println("<tr><td class='doc'>"+submitter.getEmail());
                        out.println("<input type='hidden' name='submitterId"+i+"' value="+submitter.getId()+"></td>");
                        out.println("<td><input type='text' name='gradesReceived"+i+"' value='' size=30 maxlength=5></td>");
                        out.println("<td><input type='text' name='comments"+i+"' value='' size=30 maxlength=254></td></tr>");
                        i++;
                    }
                %>
            </table>
            <br/>
            <table>
                <% if (!submittersToEvaluate.isEmpty()){%>
                    <tr>
                        <td align=center colspan=2>
                            <input type='submit' style="width: 150px; display: inline-block;" id="uploadEvals" value='Upload Evaluations' />
                        </td>
                    </tr>
                <%} else {%>
                    <tr> 
                        <td align=center colspan=2> There are no submissions to evaluate for you! </td> 
                    </tr>
                <% }%>
            </table>
            <br/>
	        <% 
	            if(message != null) {
	                out.println("<div>"+message+"</div><br/>");
	            }
	        %>
	        <a href="<%=request.getContextPath()%>/ViewSubmittableMember?memberId=<%=submittableMemberId%>">Back to <%=submittableMemberName%></a>
	        &nbsp;&nbsp;
	        <a href="<%=request.getContextPath()%>/ViewRegistrableMember?memberId=<%=activeMemberId%>">Back to <%=activeMemberName%></a>
	        <hr />
        </form>
        <hr />
        <jsp:include page="layout/footer.jsp" />
    </body>
</html>