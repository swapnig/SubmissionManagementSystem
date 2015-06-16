package edu.neu.ccis.sms.servlets.members.create;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.ContextKeys;
import edu.neu.ccis.sms.constants.MessageKeys;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl;
import edu.neu.ccis.sms.entity.users.RoleType;

/**
 * Read the attributes for the category for which member is being created, and build an html form
 * with each attribute as a text field. If the user creating member does not have conductor role on
 * the parent member then notify the user by showing a message
 * 
 * @author Swapnil Gupta
 * @date May 28, 2015
 * @lastUpdate Jun 10, 2015
 *
 */
@WebServlet("/CreateNewMemberForm")
public class CreateNewMemberFormServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateNewMemberFormServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		StringBuffer content = new StringBuffer();
		Long memberId = Long.parseLong(request.getParameter(RequestKeys.PARAM_PARENT_MEMBER_ID));
		String categoryName = request.getParameter(RequestKeys.PARAM_CATEGORY_NAME);
		String parentMemberName = request.getParameter(RequestKeys.PARAM_PARENT_MEMBER_NAME);

		UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();
		Long userId = (Long) request.getSession().getAttribute(SessionKeys.keyUserId);

		ServletContext context = getServletContext();
		HashMap<String, ArrayList<String>> categoryToHtmlLabels =
				(HashMap<String, ArrayList<String>>) context.getAttribute(ContextKeys.CATEGORY_TO_ATTRIBUTES);
		if (userToMemberMappingDao.doesUserHaveRoleForMember(userId, RoleType.CONDUCTOR, memberId)) {
			ArrayList<String> htmlLabels = categoryToHtmlLabels.get(categoryName);
			for(String htmlLabel : htmlLabels) {
				content.append("<tr><td><label style='text-transform: capitalize'>" + htmlLabel + "</label>:</td> " +
						"<td><input type='text' name='" + htmlLabel + "'></td></tr>");
			}
			content.append("<tr colspan='2'><td><input type='submit' id='createMember' value='Create " + categoryName + "'></td></tr>");
			content.append("<input type='hidden' name='category' value='" + categoryName + "' style='display:none'>");
			content.append("<input type='hidden' id ='parentMemberId' name='parentMemberId' value='" + memberId + "' style='display:none'>");

		} else {
			content.append("<font size='4' color='red'>" + MessageKeys.UNAUTHORIZED_MEMBER_CREATION + "</font>");
		}

		response.setContentType("text/html");
		response.getWriter().write(content.toString());
	}
}
