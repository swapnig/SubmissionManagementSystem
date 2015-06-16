package edu.neu.ccis.sms.servlets.members.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.MessageKeys;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.CategoryDao;
import edu.neu.ccis.sms.dao.categories.CategoryDaoImpl;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.entity.categories.Category;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberAttribute;

/**
 * Create member with the name and attributes provided by the user, if the member with the same name already
 * exists for the given parent member notify the user regarding the same
 * 
 * @author Swapnil Gupta
 * @since May 25, 2015
 * @version SMS 1.0
 *
 */
@WebServlet("/CreateMember")
public class CreateMemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateMemberServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		ArrayList<String> paramNames = Collections.list(request.getParameterNames());
		Long userId = (Long) request.getSession().getAttribute(SessionKeys.keyUserId);
		String parentMemberId = request.getParameter(RequestKeys.PARAM_PARENT_MEMBER_ID);

		MemberDao memberDao = new MemberDaoImpl();
		String memberNameParam = paramNames.get(0);
		String memberName = request.getParameter(memberNameParam);

		StringBuffer content = new StringBuffer();

		// If member with same name already exists for the parent member notify the user regarding this
		if (memberDao.doesMemberNameExistForParentMember(memberName, Long.parseLong(parentMemberId))) {
			content.append("<font size='4' color='red'>" + MessageKeys.MEMBER_EXISTS_FOR_PARENT +"</font>");
		} else {
			Member newMember = new Member();
			newMember.setName(memberName);

			CategoryDao categoryDao = new CategoryDaoImpl();
			Category category = categoryDao.getCategoryByName(request.getParameter(RequestKeys.PARAM_CATEGORY_NAME));
			newMember.setCategory(category);
			paramNames.remove(RequestKeys.PARAM_CATEGORY_NAME);

			MemberDao parentMemberDao = new MemberDaoImpl();
			Member parentMember = parentMemberDao.getMember(Long.parseLong(parentMemberId));
			newMember.setParentMember(parentMember);
			paramNames.remove(RequestKeys.PARAM_PARENT_MEMBER_ID);

			Set<MemberAttribute> memberAttributes = new HashSet<MemberAttribute>();
			for (String paramName : paramNames) {
				String paramValue = request.getParameter(paramName);;
				memberAttributes.add(new MemberAttribute(paramName, paramValue, newMember));
			}
			newMember.setAttributes(memberAttributes);

			memberDao.saveMember(newMember, userId);
			content.append(memberName + " " + MessageKeys.MEMBER_CREATED);
		}
		response.setContentType("text/html");
		response.getWriter().write(content.toString());
	}
}
