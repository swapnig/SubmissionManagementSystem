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
    edu.neu.ccis.sms.entity.users.RoleType,
    edu.neu.ccis.sms.entity.categories.UserToMemberMapping"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
    MemberDao memberDao = new MemberDaoImpl();
    UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();

	RoleType role = userToMemberMappingDao.getUsersRoleForMember(userId, activeMemberId);
    if(role == null || role == RoleType.SUBMITTER){
        response.sendRedirect("dashboard.jsp");
    }

    Member activeMember = memberDao.getMember(activeMemberId);
    Member submittableMember = memberDao.getMember(submittableMemberId);

    Set<Member> submittableMembers = memberDao.findAllSubmittableMembersByParentMemberId(activeMemberId);

    String activeMemberName = activeMember.getName();
    String submittableMemberName = submittableMember.getName();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <link href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet" type="text/css"/>
        <title>Document Retrieval Page for <%=activeMemberName%> - <%=submittableMemberName%></title>
    </head>
    <body>
        <div>Document Retrieval Page for <%=activeMemberName%> - <%=submittableMemberName%></div>
        <br/>
        <form action="<%=request.getContextPath()%>/DocumentRetrievalForEvaluation" method="POST">
            <input type="hidden" id="memberId" name="memberId" value="<%=submittableMemberId%>">
            <table>
                <tr>
                    <td>Download submissions for evaluations : </td>
                    <td align=center colspan=2>
                        <input type='submit' value="Download Submissions" style="width: 150px; display: inline-block;"/>
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
        <br/>
        <a href="<%=request.getContextPath()%>/ViewSubmittableMember?memberId=<%=submittableMemberId%>">Back to <%=submittableMemberName%></a>
        &nbsp;&nbsp;
        <a href="<%=request.getContextPath()%>/ViewRegistrableMember?memberId=<%=activeMemberId%>">Back to <%=activeMemberName%></a>
        <hr />
        <font size=2>
        NOTE : <br/>
            This page helps in downloading submission documents in zip file format for evaluation.
        </font>
        <jsp:include page="layout/footer.jsp" />
    </body>
</html>