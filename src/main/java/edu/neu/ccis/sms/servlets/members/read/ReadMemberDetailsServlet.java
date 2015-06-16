package edu.neu.ccis.sms.servlets.members.read;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.comparator.MemberAttributeComparator;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberAttribute;
import edu.neu.ccis.sms.entity.users.RoleType;

/**
 * Read all the attributes for the current member providing view access, for conductors provide
 * additional functionality to edit member attributes and add role for member
 * 
 * @author Swapnil Gupta
 * @since May 28, 2015
 * @version SMS 1.0
 *
 */
@WebServlet("/ReadMemberDetails")
public class ReadMemberDetailsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReadMemberDetailsServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		String memberId = request.getParameter(RequestKeys.PARAM_MEMBER_ID);
		MemberDao memberDao = new MemberDaoImpl();

		UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();
		Long userId = (Long) request.getSession().getAttribute(SessionKeys.keyUserId);
		Member member = memberDao.getMember(Long.parseLong(memberId));

		StringBuffer content = new StringBuffer();
		Set<MemberAttribute> memberAttributesSet = member.getAttributes();

		ArrayList<MemberAttribute> membersAttributes = new ArrayList<MemberAttribute> (memberAttributesSet);
		Collections.sort(membersAttributes, new MemberAttributeComparator());

		for (MemberAttribute memberAttribute : membersAttributes) {
			String attributeName = memberAttribute.getName();
			String attributeValue = memberAttribute.getValue();
			content.append("<tr><td><label for='" + attributeName + "'>" + attributeName + "</label></td>");
			content.append("<td><input name='" + attributeName + "' type='text' value='" + attributeValue + "' disabled/></td></tr>");
		}

		// Add additional functionalities of editing attributes and adding user roles for conductor
		if (userToMemberMappingDao.doesUserHaveRoleForMember(userId, RoleType.CONDUCTOR, member.getId())) {
			content.append("<tr><td><input id='editMemberAttributes' type='button' value='Edit'/></td>");
			content.append("<td><input type='submit' id='saveMemberAttributes' value='Update' style='display:none'/></td></tr>q");
			content.append("<tr><td><input type='hidden' name='memberId' value='" + memberId + "'/></td></tr>");
		}

		response.setContentType("text/html");
		response.getWriter().write(content.toString());
	}

}
