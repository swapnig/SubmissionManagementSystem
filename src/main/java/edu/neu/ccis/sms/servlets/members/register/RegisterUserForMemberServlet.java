package edu.neu.ccis.sms.servlets.members.register;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.ContextKeys;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.users.RoleType;

/**
 * Register given user for a given member with given role, if such user-rol-member mapping does not already exist.
 * 
 * @author Swapnil Gupta
 * @since Jun 16, 2015
 * @version SMS 1.0
 *
 */
@WebServlet("/RegisterUserForMember")
public class RegisterUserForMemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RegisterUserForMemberServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		Long memberId = Long.parseLong(request.getParameter(RequestKeys.PARAM_MEMBER_ID));
		String role = request.getParameter(RequestKeys.PARAM_USER_ROLE);
		Long userId = (Long) request.getSession(false).getAttribute(SessionKeys.keyUserId);

		ServletContext context = getServletContext();
		HashMap<String, String> roleKeyToRoles = (HashMap<String, String>) context.getAttribute(ContextKeys.ROLE_KEY_TO_ROLE);
		RoleType roletype = RoleType.valueOf(role.toUpperCase());

		UserDao userDao = new UserDaoImpl();
		UserToMemberMappingDao mapping = new UserToMemberMappingDaoImpl();
		StringBuffer content = new StringBuffer();
		if(mapping.doesUserHaveRoleForMember(userId, roletype, memberId)) {
			content.append("<font size='4' color='red'> You are already registered as a "
					+ roleKeyToRoles.get(roletype.toString().toLowerCase()) + " for this member</font>");
		} else {
			userDao.registerUserForMember(userId, memberId, roletype);
			content.append("You are now registered as a " + roleKeyToRoles.get(roletype.toString().toLowerCase()) + " for this member");
		}

		response.setContentType("text/html");
		response.getWriter().write(content.toString());
	}
}
