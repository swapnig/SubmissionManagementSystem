package edu.neu.ccis.sms.servlets;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

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
 * Servlet implementation class UpdateCOIForUserServlet
 * 
 * @author Pramod R. Khare
 * @date 6-June-2015
 * @lastUpdate 10-June-2015
 */
@WebServlet(name = "UpdateCOIForUserServlet", urlPatterns = { "/UpdateCOIForUser" })
public class UpdateCOIForUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateCOIForUserServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
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
            response.sendRedirect("pages/update_coi.jsp");
        } catch (final Exception e) {
            e.printStackTrace();
            response.sendRedirect("pages/error.jsp");
        }
    }
}
