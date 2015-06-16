package edu.neu.ccis.sms.servlets.members.update;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;

/**
 * Servlet implementation class SaveMemberServlet
 * @author Swapnil Gupta
 * @date Jun 11, 2015
 * @lastUpdate Jun 11, 2015
 *
 */
@WebServlet("/AddRoleToMember")
public class AddRoleToMemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddRoleToMemberServlet() {
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
        String email = request.getParameter(RequestKeys.PARAM_USER_EMAIL);
		
        UserDao userDao = new UserDaoImpl();
        // User being granted role
        User subUser = userDao.getUserByEmailId(email);
        
        StringBuffer content = new StringBuffer();
        if(null == subUser) {
        	content.append("<font size='4' color='red'>No user with email " + email + " found.</font>");
        } else {
        	content.append("Data updated into database");
        	userDao.registerUserForMember(subUser.getId(), memberId, RoleType.valueOf(role.toUpperCase()));
        }
		response.setContentType("text/html");
		response.getWriter().write(content.toString());
	}
}
