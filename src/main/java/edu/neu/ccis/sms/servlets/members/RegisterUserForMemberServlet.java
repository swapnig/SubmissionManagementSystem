package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.ContextKeys;
import edu.neu.ccis.sms.constants.ErrorMessageKeys;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.users.RoleType;

/**
 * Register current user for a given member with given role,
 * if such a user-role-member mapping does not already exist.
 * 
 * @author Swapnil Gupta
 * @createdOn May 16, 2015
 *
 */
@WebServlet("/RegisterUserForMember")
public class RegisterUserForMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(RegisterUserForMemberServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterUserForMemberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * When the request is received assign the current user, the given role on the given member,
     * if they do not already have that role for member, else display an error message indicating
     * that they already have the role on the member
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        // Get the member id and the role user want for the member from request parameters
        Long memberId = Long.parseLong(request.getParameter(RequestKeys.PARAM_MEMBER_ID));
        String role = request.getParameter(RequestKeys.PARAM_USER_ROLE);

        /*
         * Get the generic role from the terminology specific role, using the mapping specified in the
         * terminology, which is stored as a key value pair in a map within the servlet context
         */
        ServletContext context = getServletContext();
        @SuppressWarnings("unchecked")
        HashMap<String, String> roleKeyToRoles =
        (HashMap<String, String>) context.getAttribute(ContextKeys.ROLE_KEY_TO_ROLE);
        RoleType roletype = RoleType.valueOf(role.toUpperCase());

        // Get the userId from session, of the user who is trying to create this member.
        Long userId = (Long) request.getSession(false).getAttribute(SessionKeys.keyUserId);
        UserDao userDao = new UserDaoImpl();
        UserToMemberMappingDao mapping = new UserToMemberMappingDaoImpl();

        // Response data from the server
        StringBuffer responseData = new StringBuffer();

        /*
         * To ensure that a given user can only have a role on the user only once, we check whether the user
         * already has the requested role on the member.
         * If the user does not have the requested role on the member, add the role,
         * Else respond back with a error message indicating they already have requested role on the member
         * 
         */
        if(mapping.doesUserHaveRoleForMember(userId, roletype, memberId)) {
            responseData.append("<font size='4' color='red'>" + ErrorMessageKeys.USER_ALREADY_HAS_ROLE_FOR_MEMBER
                    + "</font>");
            LOGGER.info("User id: " + userId + " already has a "
                    + roleKeyToRoles.get(roletype.toString().toLowerCase()) + " role for member" + memberId);
        } else {
            // Register user for given member with given role
            userDao.registerUserForMember(userId, memberId, roletype);

            responseData.append("You are now registered as a " + roleKeyToRoles.get(roletype.toString().toLowerCase())
                    + " for this member");
            LOGGER.info("User id: " + userId + " was assigned "
                    + roleKeyToRoles.get(roletype.toString().toLowerCase()) + " role for member" + memberId);
        }

        response.setContentType("text/html");
        response.getWriter().write(responseData.toString());
    }
}
