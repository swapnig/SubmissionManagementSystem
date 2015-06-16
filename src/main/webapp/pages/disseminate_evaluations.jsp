<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"
    import="edu.neu.ccis.sms.constants.SessionKeys,javax.servlet.http.HttpSession,java.util.*,
    javax.servlet.http.HttpServletRequest,edu.neu.ccis.sms.dao.categories.MemberDao,
    edu.neu.ccis.sms.dao.categories.MemberDaoImpl,edu.neu.ccis.sms.entity.categories.Member,
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
    Set<Member> SubmittableMembers = memberDao.findAllSubmittableMembersByParentMemberId(activeMemberId);
    System.out.println("Total Number of submittables - " + SubmittableMembers.size());
    
    // TODO get the activeMember's name 
    String activeMemberName = "CS5500";
    String submittableMemberName = "Assignment 1";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>Disseminate Evaluations</title>
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
        <div class="form_header">Disseminate Evaluations for <%=activeMemberName%> - <%=submittableMemberName%></div>
        <hr />
        <form action="<%=request.getContextPath()%>/DisseminateEvaluations" method="POST">
            <table cellpadding="3" border="0">
                <tr>
                    <td class='label'>Select Member:</td>
                    <td>
                        <select name="submittableMemberId" style="width: 300px;">
                            <%
                                for (Member member : SubmittableMembers) {
                                    out.println("<option value=" + member.getId() + ">" + member.getName() + "</option>");
                                }
                            %>
                        </select>
                    </td>
                </tr>
                <tr>
                    <td class='label'>Select Final Evaluations type:</td>
                    <td><select id="evalType" name="evalType" style="width: 300px;">
                        <option value="AVERAGE">AVERAGE</option>
                        <option value="MINIMUM">MINIMUM</option>
                        <option value="MAXIMUM">MAXIMUM</option>
                    </select></td>
                </tr>
            </table>
            <br/>
            <br/>
            <font size=2>
            Evaluation Types: <br/>
            1. AVERAGE : Calculates final evaluations for all submissions as average of all individual evaluations available for that document<br/>
            2. MINIMUM : Calculates final evaluations for all submissions as minimum of all individual evaluations available for that document<br/>
            3. MAXIMUM : Calculates final evaluations for all submissions as maximum of all individual evaluations available for that document<br/>
            </font>
            <br/>
            <table>
                <tr>
                    <td align=center colspan=2>
                        <input type='submit' value="Disseminate Evaluations" style="width: 180px; display: inline-block;"/>
                    </td>
                </tr>
            </table>
        </form>
        <hr />
    </body>
</html>