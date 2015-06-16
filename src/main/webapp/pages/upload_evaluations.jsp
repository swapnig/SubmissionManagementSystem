<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="edu.neu.ccis.sms.constants.SessionKeys,
    javax.servlet.http.HttpSession,java.util.*,
    javax.servlet.http.HttpServletRequest,
    edu.neu.ccis.sms.dao.categories.*,
    edu.neu.ccis.sms.dao.users.*,
    edu.neu.ccis.sms.entity.categories.*,
    edu.neu.ccis.sms.entity.users.*"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    // Get the current user id
    Long userId = (Long) session.getAttribute(SessionKeys.keyUserId);

    // Load all the submittable Member Details
    Long activeMemberId = (Long) session.getAttribute(SessionKeys.activeMemberId);
    System.out.println("Session activeMemberId - " + activeMemberId);

    // MemberDao memDao = new MemberDaoImpl();
    // Member activeMember = memDao.getMember(activeMemberId);
    // String activeMemberName = activeMember.getName();

    // Long submittableMemberId = (Long) request.getParameter("submittableMemberId");
    // Member submittableMember = memDao.getMember(submittableMemberId);
    // String submittableMemberName = submittableMember.getName();

    // TODO get the activeMember's name 
    String activeMemberName = "CS5500";
    String submittableMemberName = "Assignment 1";

    // Get the usersToEvaluate for this RoleType.EVALUATOR user
    Long submittableMemberId = new Long(3);
    UserDao userDao = new UserDaoImpl();
    User user = userDao.getUserByIdWithSubmittersToEvaluateMappings(userId);
    Set<User> submittersToEvaluate = user.getSubmittersToEvaluateForMemberId(submittableMemberId);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Upload Evaluations</title>
        <style>
            body {
                font-family: 'Roboto', sans-serif;
            }
            
            .form_header {
                font-weight: 100;
                text-align: left;
                font-size: 1.8em;
            }
            
            td.doc {
                color: navy;
                font-size: 150%;
            }
            
            td.label {
                font-size: 150%;
            }
            
            th.label {
                font-size: 150%;
            }
            
            div {
                color: black;
                font-size: 150%;
            }
            
            div.error {
                color: red;
                font-size: 200%;
            }
        </style>
    </head>
    <body>
        <div class="form_header">Upload Evaluations for <%=activeMemberName%> - <%=submittableMemberName%></div>
        <hr />
        <form action="<%=request.getContextPath()%>/UploadEvaluations" method="POST">
            <label style="width:150px;display:inline-block;" for="maxGrades">Maximum Grades (Out of total)</label>
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
                   <tr> <td align=center colspan=2> There are no submissions to evaluate for you! </td> </tr>
                <% }%>
            </table>
        </form>
        <hr />
    </body>
</html>