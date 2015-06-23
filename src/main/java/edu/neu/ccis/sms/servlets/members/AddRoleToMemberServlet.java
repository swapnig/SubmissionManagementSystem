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
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;

/**
 * Adds given role for the member to the user identified by its email id,
 * if such a user-role-member mapping does not already exist.
 * 
 * Authentication to perform this action is done from the front end.
 * 
 * @author Swapnil Gupta
 * @createdOn Jun 16, 2015
 *
 */
@WebServlet("/AddRoleToMember")
public class AddRoleToMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AddRoleToMemberServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddRoleToMemberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * When the request is received assign the given user, the given role on the given member,
     * if they do not already have that role for member, else display an error message indicating
     * that they already have the role on the member.
     * 
     * If the member with given email id does not exist return an error message indicating that
     * no user with given email exists
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */

    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        /*
         * Get the member id, email of the user which is to be assigned the role,\
         * the role to be assigned from request parameters
         */
        Long memberId = Long.parseLong(request.getParameter(RequestKeys.PARAM_MEMBER_ID));
        String role = request.getParameter(RequestKeys.PARAM_USER_ROLE);
        String email = request.getParameter(RequestKeys.PARAM_USER_EMAIL);

        /*
         * Get the generic role from the terminology specific role, using the mapping specified in the
         * terminology, which is stored as a key value pair in a map within the servlet context
         */
        ServletContext context = getServletContext();
        @SuppressWarnings("unchecked")
        HashMap<String, String> roleKeyToRoles = (HashMap<String, String>) context.getAttribute(ContextKeys.ROLE_KEY_TO_ROLE);
        RoleType roletype = RoleType.valueOf(role.toUpperCase());

        // Response data from the server
        StringBuffer responseData = new StringBuffer();

        /*
         * Get the user which is to be assigned a role on the member, form their email id
         */
        UserDao userDao = new UserDaoImpl();
        User subUser = userDao.getUserByEmailId(email);

        /*
         * Check whether a user with given email id exists for the application,
         * If such a user proceed forward in assigning the role.
         * Else show an error message indicating that no user with given email id exists
         */
        if(null == subUser) {
            responseData.append("<font size='4' color='red'> " + email + ": "
                    + ErrorMessageKeys.NO_USER_WITH_EMAIL + "</font>");
            LOGGER.info(email + ": " + ErrorMessageKeys.NO_USER_WITH_EMAIL);
        } else {
            UserToMemberMappingDao mapping = new UserToMemberMappingDaoImpl();

            /*
             * To ensure that a given user can only have a role on the user only once, we check whether the user
             * already has the requested role on the member.
             * If the user does not have the requested role on the member, add the role,
             * Else respond back with a error message indicating they already have requested role on the member
             * 
             */
            if(mapping.doesUserHaveRoleForMember(subUser.getId(), roletype, memberId)) {
                responseData.append("<font size='4' color='red'>" + ErrorMessageKeys.OTHER_USER_ALREADY_HAS_ROLE_FOR_MEMBER
                        + "</font>");
                LOGGER.info("Attempt to make user with email '" + email + "failed as the user already has"
                        + role + " for this member");
            } else {
                // Register user for given member with given role
                userDao.registerUserForMember(subUser.getId(), memberId, roletype);

                responseData.append(email + " is now a " + role + " for this member");
                LOGGER.info("User id: " + subUser.getId() + " was assigned "
                        + roleKeyToRoles.get(roletype.toString().toLowerCase()) + " role for member" + memberId);
            }
        }
        response.setContentType("text/html");
        response.getWriter().write(responseData.toString());
    }
}
