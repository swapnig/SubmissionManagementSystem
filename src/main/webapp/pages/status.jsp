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
    edu.neu.ccis.sms.entity.categories.Member,
    edu.neu.ccis.sms.entity.submissions.Document,
    edu.neu.ccis.sms.entity.submissions.Evaluation,
    edu.neu.ccis.sms.entity.users.User"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    /* Load all the submittable Member Details */
    Long activeMemberId = (Long) session.getAttribute(SessionKeys.activeMemberId);
    System.out.println("Session activeMemberId - " + activeMemberId);

    //TODO Remove once fully tested
    activeMemberId = new Long(2);

    // Get the MemberDaoImple instance
    MemberDao memberDao = new MemberDaoImpl();
    Set<Member> submittableMembers = memberDao.findAllSubmittableMembersByParentMemberId(activeMemberId);
    System.out.println("Total Number of submittables - " + submittableMembers.size());

    // TODO get the activeMember's name 
    String activeMemberName = "CS5500";

    // Get current logged in users all submission documents
    Long userId = (Long)session.getAttribute(SessionKeys.keyUserId);
    UserDao userDao = new UserDaoImpl();
    User thisUser = userDao.getUserByIdWithSubmissions(userId);
    Set<Document> userSubmissions = thisUser.getSubmissions();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Submission Evaluations Status Page</title>
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
        <div class="form_header">Submissions Evaluations Status for <%=activeMemberName%></div>
        <hr />
        <font size=2>
            NOTE: <br/>
            1. This page shows final evaluation results for all evaluated submissions.<br/>
            2. NA - This status means the submission is yet to be evaluated.<br/>
            2. All grades are in percentage values (i.e. out of 100).<br/>
        </font>
        <table cellpadding="3" border="2">
            <tr>
                <th>Name</th>
                <th>Grades Received</th>
                <th>Comments</th>
            </tr>
            <%-- Create individual rows dynamically for each submittable member --%>
            <%
                boolean submissionFound = false;
                for (Member member : submittableMembers) {
                    submissionFound = false;
                    out.println("<tr><td class='doc'>"+member.getName()+"</td>");
                    for(Document doc : userSubmissions){
                        if(doc.getSubmittedForMember().getId().equals(member.getId())){
                            submissionFound = true;
                            Evaluation finalEval = doc.getFinalEvaluation();
                            out.println("<td>"+finalEval.getResult()+"</td>");
                            out.println("<td>"+finalEval.getComments()+"</td></tr>");
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
        <br/>
        <hr />
    </body>
</html>