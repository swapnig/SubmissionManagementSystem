package edu.neu.ccis.sms.servlets.members.register;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.entity.users.RoleType;

import edu.neu.ccis.sms.constants.RequestKeys;

/**
 * Servlet implementation class RegisterUserForMemberServlet
 * @author Swapnil Gupta
 * @date Jun 11, 2015
 * @lastUpdate Jun 11, 2015
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Long memberId = Long.parseLong(request.getParameter(RequestKeys.PARAM_MEMBER_ID));
		String role = request.getParameter(RequestKeys.PARAM_USER_ROLE);
		Long userId = (Long) request.getSession().getAttribute(SessionKeys.keyUserId);
		
		UserDao userDao = new UserDaoImpl();
		userDao.registerUserForMember(userId, memberId, RoleType.valueOf(role.toUpperCase()));
		
		StringBuffer content = new StringBuffer();
		content.append("Data updated into database");
		response.setContentType("text/html");
		response.getWriter().write(content.toString());
	}
}
