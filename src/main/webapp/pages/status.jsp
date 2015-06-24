<%@page import="edu.neu.ccis.sms.entity.submissions.Evaluation"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="edu.neu.ccis.sms.constants.SessionKeys,
    javax.servlet.http.HttpSession,java.util.*,
    javax.servlet.http.HttpServletRequest,
    edu.neu.ccis.sms.dao.categories.MemberDao,
    edu.neu.ccis.sms.dao.users.UserDao, 
    edu.neu.ccis.sms.dao.users.UserDaoImpl,
    edu.neu.ccis.sms.dao.categories.MemberDaoImpl,
    edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao,
    edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl,
    edu.neu.ccis.sms.entity.categories.Member,
    edu.neu.ccis.sms.entity.submissions.Document,
    edu.neu.ccis.sms.entity.submissions.Evaluation,
    edu.neu.ccis.sms.entity.users.User,
    edu.neu.ccis.sms.entity.users.RoleType"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:include page="layout/header.jsp" />
<jsp:include page="layout/nav.jsp" />
<%
	/* Load all the submittable Member Details */
	Long activeMemberId = (Long) session.getAttribute(SessionKeys.activeMemberId);

	// Get current logged in users all submission documents
    Long userId = (Long)session.getAttribute(SessionKeys.keyUserId);

	// user object from session
	User user = (User) session.getAttribute(SessionKeys.keyUserObj);

    // DAOs
    UserDao userDao = new UserDaoImpl();
    MemberDao memberDao = new MemberDaoImpl();
    UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();

    Member activeMember = memberDao.getMember(activeMemberId);
    Set<Member> submittables = memberDao.findAllSubmittableMembersByParentMemberId(activeMemberId);
    List<Member> submittableMembers = new ArrayList<Member>(submittables);
    Collections.sort(submittableMembers);

    String activeMemberName = activeMember.getName();

    RoleType role = userToMemberMappingDao.getUsersRoleForMember(userId, activeMemberId);
    // if user doesn't have any role, then forward him to DashBoard page
    if(role == null){
        response.sendRedirect("dashboard.jsp");
    }

    List<User> submittersList = new ArrayList<User>();
    // Depending upon the role - user should see only his submission's evaluations
    // Or should see all submitter's details
    if (role == RoleType.CONDUCTOR || role == RoleType.EVALUATOR){
        submittersList.addAll(memberDao.getSubmittersForMemberId(activeMemberId));
    }else if (role == RoleType.SUBMITTER) {
        submittersList.add(user);
    }
    Collections.sort(submittersList);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <link href="${pageContext.request.contextPath}/resources/css/style.css" rel="stylesheet" type="text/css"/>
        <title>Submission Evaluations Status Page</title>
    </head>
    <body>
        <div class="form_header">Submissions Evaluations Status for <%=activeMemberName%></div>
        <br/>
        <% for(User submitter : submittersList) { %>
        <div>Username: <%=submitter.getFirstname()%>&nbsp;<%=submitter.getLastname()%></div>
        <div>Email: <%=submitter.getEmail()%></div> 
        <table cellpadding="3" border="2">
            <tr>
                <th>Name</th>
                <th>Grades Received</th>
                <th>Last Submitted On</th>
            </tr>
            <%-- Create individual rows dynamically for each submittable member --%>
            <%
                User thisUser = userDao.getUserByIdWithSubmissions(submitter.getId());
                Set<Document> userSubmissions = thisUser.getSubmissions();
                boolean submissionFound = false;
                for (Member member : submittableMembers) {
                    submissionFound = false;
                    out.println("<tr><td>"+member.getName()+"</td>");
                    for(Document doc : userSubmissions){
                        if(doc.getSubmittedForMember().getId().equals(member.getId())){
                            submissionFound = true;
                            Evaluation finalEval = doc.getFinalEvaluation();
                            if(finalEval == null){
                                out.println("<td>NA</td>");
                                out.println("<td>"+doc.getSubmittedOnTimestamp()+"</td></tr>");
                            }else{
                                out.println("<td>"+finalEval.getResult()+"</td>");
                                out.println("<td>"+doc.getSubmittedOnTimestamp()+"</td></tr>");
                            }
                            break;
                        }
                    }

                    if(!submissionFound){
                        // put "NA" in grades column
                        out.println("<td>NA</td><td>&nbsp;</td></tr>");
                    }
                }
            %>
        </table>
        <br/>
        <% } %>
        <br/>
        <a href="<%=request.getContextPath()%>/ViewRegistrableMember?memberId=<%=activeMemberId%>">Back to <%=activeMemberName%></a>
        <hr />
        <font size=2>
            NOTE: <br/>
            1. This page shows final evaluation results for all evaluated submissions.<br/>
            2. NA - This status means the submission is yet to be evaluated.<br/>
            2. All grades are in percentage values (i.e. out of 100).<br/>
        </font>
        <jsp:include page="layout/footer.jsp" />
    </body>
</html>