<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="edu.neu.ccis.sms.constants.SessionKeys,
    javax.servlet.http.HttpSession,java.util.*,
    javax.servlet.http.HttpServletRequest,
    edu.neu.ccis.sms.dao.categories.MemberDao,
    edu.neu.ccis.sms.dao.categories.MemberDaoImpl,
    edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao,
    edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl,
    edu.neu.ccis.sms.entity.categories.Member,
    edu.neu.ccis.sms.entity.users.User,
    edu.neu.ccis.sms.entity.users.RoleType"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="member/templates/header.jsp" />
<%
	// Check if there is any message to show in page
	String message = (String) request.getAttribute("message");

    // Get the current user id
    Long userId = (Long) session.getAttribute(SessionKeys.keyUserId);
    System.out.println("Session userId - " + userId);

    // Load all the submittable Member Details
    Long activeMemberId = (Long) session.getAttribute(SessionKeys.activeMemberId);
    System.out.println("Session activeMemberId - " + activeMemberId);

    Long submittableMemberId = (Long) session.getAttribute(SessionKeys.activeSubmittableMemberId);
    System.out.println("Session activeSubmittableMemberId - " + activeMemberId);

    // Get the MemberDaoImple instance
    MemberDao memberDao = new MemberDaoImpl();
    UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();

    RoleType role = userToMemberMappingDao.getUsersRoleForMember(userId, activeMemberId);
    if(role == null || role == RoleType.SUBMITTER){
        // Invalid page access - redirect him to his dashboard page
        response.sendRedirect("dashboard.jsp");
    }

    Member activeMember = memberDao.getMember(activeMemberId);
    Member submittableMember = memberDao.getMember(submittableMemberId);
    Set<Member> SubmittableMembers = memberDao.findAllSubmittableMembersByParentMemberId(activeMemberId);

    String activeMemberName = activeMember.getName();
    String submittableMemberName = submittableMember.getName();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Disseminate Evaluations for <%=activeMemberName%> - <%=submittableMemberName%></title>
    </head>
    <body>
        <div>Disseminate Evaluations for <%=activeMemberName%> - <%=submittableMemberName%></div>
        <br/>
        <form action="<%=request.getContextPath()%>/DisseminateEvaluations" method="POST">
            <input type="hidden" id="submittableMemberId" name="submittableMemberId" value="<%=submittableMemberId%>">
            <table cellpadding="3" border="0">
                <tr>
                    <td>Select Final Evaluations Calculations Type:</td>
                    <td><select id="evalType" name="evalType" style="width: 300px;">
                        <option value="AVERAGE">Average</option>
                        <option value="MINIMUM">Minimum</option>
                        <option value="MAXIMUM">Maximum</option>
                    </select></td>
                </tr>
            </table>
            <table>
                <tr>
                    <td align=center colspan=2>
                        <input type='submit' value="Disseminate Evaluations" style="width: 180px; display: inline-block;"/>
                    </td>
                </tr>
            </table>
        </form>
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
        <font size=2>
        Evaluation Types: <br/>
        1. Average : Calculates the final evaluations for each individual submission as an average of all individual evaluations available for that submission.<br/>
        2. Minimum : Calculates the final evaluations for each individual submission as minimum of all individual evaluations available for that submission.<br/>
        3. Maximum : Calculates the final evaluations for each individual submission as maximum of all individual evaluations available for that submission.<br/>
        </font>
        <br/>
    </body>
</html>