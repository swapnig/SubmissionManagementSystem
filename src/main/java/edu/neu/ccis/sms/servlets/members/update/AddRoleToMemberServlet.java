package edu.neu.ccis.sms.servlets.members.update;

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
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;

/**
 * FOr the given member grant the currrent user the given role
 * @author Swapnil Gupta
 * @since Jun 16, 2015
 * @version SMS 1.0
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
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        Long memberId = Long.parseLong(request.getParameter(RequestKeys.PARAM_MEMBER_ID));
        String role = request.getParameter(RequestKeys.PARAM_USER_ROLE);
        String email = request.getParameter(RequestKeys.PARAM_USER_EMAIL);

        UserDao userDao = new UserDaoImpl();
        // User being granted role
        User subUser = userDao.getUserByEmailId(email);

        ServletContext context = getServletContext();
        HashMap<String, String> roleKeyToRoles = (HashMap<String, String>) context.getAttribute(ContextKeys.ROLE_KEY_TO_ROLE);
        RoleType roletype = RoleType.valueOf(role.toUpperCase());

        StringBuffer content = new StringBuffer();
        if(null == subUser) {
            content.append("<font size='4' color='red'>No user with email " + email + " found.</font>");
        } else {
            UserToMemberMappingDao mapping = new UserToMemberMappingDaoImpl();
            System.out.println("UserId:" + subUser.getId() + " Roletype:" + roletype + " MemberId:" + memberId);
            if(mapping.doesUserHaveRoleForMember(subUser.getId(), roletype, memberId)) {
                content.append("<font size='4' color='red'> The user is already registered as a "
                        + roleKeyToRoles.get(roletype.toString().toLowerCase()) + " for this member</font>");
            } else {
                userDao.registerUserForMember(subUser.getId(), memberId, roletype);
                content.append(email + " is now registered as a " + roleKeyToRoles.get(roletype.toString().toLowerCase())
                        + " for this member");
            }
        }
        response.setContentType("text/html");
        response.getWriter().write(content.toString());
    }
}
