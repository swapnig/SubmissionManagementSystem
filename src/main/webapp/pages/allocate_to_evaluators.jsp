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

    Set<User> evaluators = memberDao.getEvaluatorsForMemberId(activeMemberId);
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
		<div class="form_header">Allocate Submissions To Evaluators for CS5500</div>
		<!-- <%=request.getParameter("currentMemberId")%> -->
		<hr />
		<form action="<%=request.getContextPath()%>/AllocateToEvaluators" method="POST">
			<table cellpadding="3" border="0">
				<tr>
					<td class='label'>Select Member:</td>
					<td><select id="member" name="memberId" style="width: 300px;">
							<%
							    for (Member member : SubmittableMembers) {
							        out.println("<option value=" + member.getId() + ">" + member.getName() + "</option>");
							    }
							%>
					</select></td>
				</tr>
				<tr>
	                <td class='label'>Select number of evaluators per submission:</td>
	                <td><select id="numberOfEvaluatorsPerSub" name="numberOfEvaluatorsPerSub" style="width: 300px;">
	                        <%
	                            for (int i=1; i<=evaluators.size(); i++) {
	                                out.println("<option value=" + i + ">" + i + "</option>");
	                            }
	                        %>
	                </select></td>
	            </tr>
			</table>
	        <br/>
	        <font size=2>Click below to perform automatic allocation of submissions for grading to evaluators</font>
			<table>
			    <tr>
	                <td align=center colspan=2>
	                    <input type='submit' value="Allocate Evaluators" style="width: 150px; display: inline-block;"/>
	                </td>
	            </tr>
	        </table>
		</form>
		<hr />
    </body>
</html>