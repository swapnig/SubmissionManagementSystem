<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="edu.neu.ccis.sms.constants.SessionKeys,javax.servlet.http.HttpSession,java.util.*,
    javax.servlet.http.HttpServletRequest,edu.neu.ccis.sms.dao.categories.MemberDao,
    edu.neu.ccis.sms.dao.categories.MemberDaoImpl,edu.neu.ccis.sms.entity.categories.Member,
    edu.neu.ccis.sms.entity.users.User, edu.neu.ccis.sms.entity.users.RoleType,
    edu.neu.ccis.sms.entity.categories.UserToMemberMapping"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    /* Load all the submittable Member Details */
    Long activeMemberId = (Long) session.getAttribute(SessionKeys.activeMemberId);
    System.out.println("Session activeMemberId - " + activeMemberId);

    //TODO Remove once fully tested
    activeMemberId = new Long(2);

    Set<Member> submittableMembers = null;
    // Get the MemberDaoImple instance
    MemberDao memberDao = new MemberDaoImpl();
    submittableMembers = memberDao.findAllSubmittableMembersByParentMemberId(activeMemberId);
    System.out.println("Total Number of submittables - " + submittableMembers.size());

    // TODO This page should only open for Evaluator role for current active member id
    // User user = (User) session.getAttribute(SessionKeys.keyUserObj);
    // if(!user.isEvaluatorForMemberId(activeMemberId)){
    //    response.sendRedirect("error.jsp");
    // }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Allocate Submissions To Evaluators</title>
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
        <div class="form_header">Document Retrieval page for CS5500</div>
        <!-- <%=request.getParameter("currentMemberId")%> -->
        <hr />
        <form action="<%=request.getContextPath()%>/DocumentRetrievalForEvaluation" method="POST">
            <table cellpadding="3" border="0">
                <tr>
                    <td class='label'>Select Member:</td>
                    <td><select id="member" name="memberId" style="width: 300px;">
                            <%
                                for (Member member : submittableMembers) {
                                    out.println("<option value=" + member.getId() + ">" + member.getName() + "</option>");
                                }
                            %>
                    </select></td>
                </tr>
            </table>
            <br/>
            <table>
                <tr>
                    <td><font size=2>**Click to download the submission document zip for evaluation</font></td>
                    <td align=center colspan=2>
                        <input type='submit' value="Download Submissions" style="width: 150px; display: inline-block;"/>
                    </td>
                </tr>
            </table>
        </form>
        <hr />
    </body>
</html>