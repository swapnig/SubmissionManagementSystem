package edu.neu.ccis.sms.servlets;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.users.User;

/**
 * Servlet implementation class UpdateCOIForUserServlet; Saves user's conflict of interests with other users in the
 * system; Takes other user's email ids with whom this logged-in user has Conflict of Interest with.
 * 
 * This servlet can be used to Add, modify and delete - user ids from Conflicts of Interest list of users
 * 
 * Takes request parameters:
 * 
 * 1) "submitType" - There can be 3 submit types - a) "Add" - To add new users to conflicts of interest list, b)
 * "Replace" - Modify conflicts of interest list - here it removes old list of users with new list of users, c) "Clear"
 * - To delete all users from conflicts of interest list
 * 
 * 2) "coifield" - email ids of users with whom this user has conflicts of interest with
 * 
 * @author Pramod R. Khare
 * @date 6-June-2015
 * @lastUpdate 10-June-2015
 */
@WebServlet(name = "UpdateCOIForUserServlet", urlPatterns = { "/UpdateCOIForUser" })
public class UpdateCOIForUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String coiJspView = "pages/update_coi.jsp";
    private static final String errorPageJspView = "pages/error.jsp";
    private static final Logger LOGGER = Logger.getLogger(UpdateCOIForUserServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateCOIForUserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Forwards to doPost(request, response) method, See javadoc comments of
     * {@link #doPost(HttpServletRequest, HttpServletResponse)}
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * Takes user's email-ids with whom this logged-in user has conflicts of interest with; This servlet can be used to
     * Add, modify and delete - user ids from Conflicts of Interest list of users
     * 
     * Takes request parameters:
     * 
     * 1) "submitType" - There can be 3 submit types - a) "Add" - To add new users to conflicts of interest list, b)
     * "Replace" - Modify conflicts of interest list - here it removes old list of users with new list of users, c)
     * "Clear" - To delete all users from conflicts of interest list
     * 
     * 2) "coifield" - email ids of users with whom this user has conflicts of interest with
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        LOGGER.info("Method - UpdateCOIForUserServlet:doPost");

        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute(SessionKeys.keyUserId);

        try {
            UserDao userDao = new UserDaoImpl();
            User one = userDao.getUser(userId);

            Set<User> oldCoiSet = one.getMyConflictsOfInterestWithUsers();

            // Check what is the submitType of request
            String submitType = request.getParameter("submitType");

            Enumeration<String> paramNames = request.getParameterNames();
            Set<User> newCoiSet = new HashSet<User>();
            while (paramNames.hasMoreElements()) {
                String param = paramNames.nextElement();
                if (param.startsWith("coifield")) {
                    String coiUserEmailId = request.getParameter(param);
                    User coiUser = userDao.getUserByEmailId(coiUserEmailId);
                    // Make sure that its a valid user and that user is not himself
                    if (coiUser != null) {
                        newCoiSet.add(coiUser);
                    }
                }
            }

            // Default action is Replace the old topics with new ones
            if (null == submitType || submitType.startsWith("Replace")) {
                oldCoiSet.clear();
                oldCoiSet.addAll(newCoiSet);
                one.setMyConflictsOfInterestWithUsers(oldCoiSet);
                userDao.updateUser(one);
            } else if (submitType.startsWith("Clear")) {
                // Remove the old conflicts of interest
                oldCoiSet.clear();
                one.setMyConflictsOfInterestWithUsers(oldCoiSet);
                userDao.updateUser(one);
            } else if (submitType.startsWith("Add")) {
                oldCoiSet.addAll(newCoiSet);
                one.setMyConflictsOfInterestWithUsers(oldCoiSet);
                userDao.updateUser(one);
            }

            response.sendRedirect(coiJspView);
        } catch (final Exception e) {
            e.printStackTrace();
            LOGGER.info("Failed to update Conflict of interest : " + e.getMessage());
            response.sendRedirect(errorPageJspView);
        }
    }
}
