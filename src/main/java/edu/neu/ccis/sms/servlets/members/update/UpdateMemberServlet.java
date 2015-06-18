package edu.neu.ccis.sms.servlets.members.update;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberAttribute;
import edu.neu.ccis.sms.util.PrintInfo;

/**
 * Servlet implementation class SaveMemberServlet
 * @author Swapnil Gupta
 * @date Jun 11, 2015
 * @lastUpdate Jun 11, 2015
 *
 */
@WebServlet("/UpdateMember")
public class UpdateMemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateMemberServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		PrintInfo.printRequestParameters(request);
		ArrayList<String> paramNames = Collections.list(request.getParameterNames());

		MemberDao memberDao = new MemberDaoImpl();
		Long memberId = Long.parseLong(request.getParameter(RequestKeys.PARAM_MEMBER_ID));
		Member existingMember = memberDao.getMember(memberId);

		String memberName = null;
		if (paramNames.contains(RequestKeys.PARAM_MEMBER_NAME)) {
			memberName = request.getParameter(RequestKeys.PARAM_MEMBER_NAME);
			existingMember.setName(memberName);
		}

		StringBuffer content = new StringBuffer();

		if (memberDao.doesMemberNameExistForParentMember(memberName, null)) {
			content.append("<font size='4' color='red'>Member with name " + memberName +
					" already exists for its parent member, choose a different name.</font>");
		} else {
			Set<MemberAttribute> memberAttributes = existingMember.getAttributes();
			for (MemberAttribute memberAttribute : memberAttributes) {
				String attributeName = memberAttribute.getName();
				if (paramNames.contains(attributeName)) {
					memberAttribute.setValue(request.getParameter(attributeName));
				}
			}
			existingMember.setAttributes(memberAttributes);
			try {
                memberDao.updateMember(existingMember);
                content.append("Details have been updated for " + memberName);
            } catch (Exception e) {
                e.printStackTrace();
                content.append("Failed to update the details for " + memberName);
            }
		}
		response.setContentType("text/html");
		response.getWriter().write(content.toString());
	}
}
