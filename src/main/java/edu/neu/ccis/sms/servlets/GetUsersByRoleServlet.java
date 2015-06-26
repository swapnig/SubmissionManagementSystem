package edu.neu.ccis.sms.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;

/**
 * Servlet implementation class GetUsersByRoleServlet; This servlet gives the list of users who have registered for a
 * given registerable member and have given role.
 * 
 * Servlet takes two request parameters :
 * 
 * 1) memberId - id of a registerable member for which we are retrieving users list
 * 
 * 2) roleType - role for which we are retrieving users list
 * 
 * @author Pramod R. Khare
 * @date 19-June-2015
 */
@WebServlet(name = "GetUsersByRoleServlet", urlPatterns = { "/GetUsersByRoleServlet" })
@MultipartConfig
public class GetUsersByRoleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(GetUsersByRoleServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetUsersByRoleServlet() {
        super();
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
     * Get the user's list by their role for a registerable member;
     * 
     * Takes two request parameters :
     * 
     * 1) memberId - id of a registerable member for which we are retrieving users list
     * 
     * 2) roleType - role for which we are retrieving users list
     * 
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        LOGGER.info("Method - GetUsersByRoleServlet:doPost");
        try {
            // DAO impls
            MemberDao memberDao = new MemberDaoImpl();

            HttpSession session = request.getSession(false);
            Long userId = (Long) session.getAttribute(SessionKeys.keyUserId);

            // Get parameter "memberId" for which to get the user's list
            // Long activeMemberId = Long.parseLong(request.getParameter("memberId"));
            Long activeMemberId = (Long) session.getAttribute(SessionKeys.activeMemberId);
            RoleType role = RoleType.valueOf(request.getParameter("roleType"));

            List<User> usersList = null;
            if (role == RoleType.EVALUATOR) {
                usersList = new ArrayList<User>(memberDao.getEvaluatorsForMemberId(activeMemberId));
            } else if (role == RoleType.SUBMITTER) {
                usersList = new ArrayList<User>(memberDao.getSubmittersForMemberId(activeMemberId));
            } else if (role == RoleType.CONDUCTOR) {
                usersList = new ArrayList<User>(memberDao.getConductorsForMemberId(activeMemberId));
            }

            // Create the output html string

            LOGGER.info("GetUsersListByRole successfully retrieved!");
        } catch (Exception ex) {
            request.setAttribute("message", "Failed to retrieve users list : " + ex.getMessage());
            LOGGER.info("Failed to retrieve users list : " + ex.getMessage());
            // send the error message in response
        }
    }
}
