package edu.neu.ccis.sms.servlets.members.read;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.comparator.MemberComparator;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;

/**
 * Read all the submittables for given member sorted by their name
 * 
 * @author Swapnil Gupta
 * @since Jun 10, 2015
 * @version SMS 1.0
 *
 */
@WebServlet("/ReadSubmittablesForMember")
public class ReadSubmittablesForMemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ReadSubmittablesForMemberServlet() {
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
		ArrayList<Member> childSubmittablesForMember =
				new ArrayList<Member> (memberDao.findAllSubmittableMembersByParentMemberId(Long.parseLong(memberId)));
		Collections.sort(childSubmittablesForMember, new MemberComparator());
		StringBuffer content = new StringBuffer();

		content.append("<ul>");
		for (Member childSubmittable : childSubmittablesForMember) {
			String childSubmittableName = childSubmittable.getName();
			content.append("<li><a href='/SMS/ViewSubmittableMember?memberId=" + childSubmittable.getId() + "'>" + childSubmittableName + "</a></li>");
		}
		content.append("</ul>");

		response.setContentType("text/html");
		response.getWriter().write(content.toString());
	}
}
