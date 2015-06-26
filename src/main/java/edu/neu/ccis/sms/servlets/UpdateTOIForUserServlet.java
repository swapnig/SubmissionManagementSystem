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
 * Servlet implementation class UpdateTOIForUserServlet; Saves user's topics of interests/preferences as list of strings
 * into persistent store; Using this servlet we can Add, Modify or Delete the topics of preferences
 * 
 * This servlet can be used to Add, modify and delete - topics of interests/preferences list
 * 
 * Takes request parameters:
 * 
 * 1) "submitType" - There can be 3 submit types - a) "Add" - To add new topics to topics of interests/preferences list,
 * b) "Replace" - Modify topics of interests/preferences list - here it removes old list of topics with new list of
 * topics, c) "Clear" - To delete all old topics of interests/preferences
 * 
 * 2) "toifield" - topic name to be added/modifed into topics of interests/preferences list
 * 
 * @author Pramod R. Khare
 * @date 2-June-2015
 * @lastUpdate 8-June-2015
 */
@WebServlet(name = "UpdateTOIForUserServlet", urlPatterns = { "/UpdateTOIForUser" })
public class UpdateTOIForUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final String toiJspView = "pages/update_toi.jsp";
    private static final String errorPageJspView = "pages/error.jsp";
    private static final Logger LOGGER = Logger.getLogger(UpdateCOIForUserServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateTOIForUserServlet() {
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
     * Saves user's topics of interests/preferences as list of strings into persistent store; Using this servlet we can
     * Add, Modify or Delete the topics of preferences
     * 
     * This servlet can be used to Add, modify and delete - topics of interests/preferences list
     * 
     * Takes request parameters:
     * 
     * 1) "submitType" - There can be 3 submit types - a) "Add" - To add new topics to topics of interests/preferences
     * list, b) "Replace" - Modify topics of interests/preferences list - here it removes old list of topics with new
     * list of topics, c) "Clear" - To delete all old topics of interests/preferences
     * 
     * 2) "toifield" - topic name to be added/modifed into topics of interests/preferences list
     * 
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        LOGGER.info("Method - UpdateTOIForUserServlet:doPost");

        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute(SessionKeys.keyUserId);

        try {
            UserDao userDao = new UserDaoImpl();
            User one = userDao.getUser(userId);

            Set<String> oldToiSet = one.getTopicsOfInterest();

            // Check what is the submitType of request
            String submitType = request.getParameter("submitType");

            Enumeration<String> paramNames = request.getParameterNames();
            Set<String> newToiSet = new HashSet<String>();
            while (paramNames.hasMoreElements()) {
                String param = paramNames.nextElement();
                if (param.startsWith("toifield")) {
                    String topic = request.getParameter(param);
                    newToiSet.add(topic);
                }
            }

            // Default action is Replace the old topics with new ones
            if (null == submitType || submitType.startsWith("Replace")) {
                oldToiSet.clear();
                oldToiSet.addAll(newToiSet);
                one.setTopicsOfInterest(oldToiSet);
                userDao.updateUser(one);
            } else if (submitType.startsWith("Clear")) {
                // Remove the old topics of preferences
                oldToiSet.clear();
                one.setTopicsOfInterest(oldToiSet);
                userDao.updateUser(one);
            } else if (submitType.startsWith("Add")) {
                oldToiSet.addAll(newToiSet);
                one.setTopicsOfInterest(oldToiSet);
                userDao.updateUser(one);
            }

            response.sendRedirect(toiJspView);
        } catch (final Exception e) {
            e.printStackTrace();
            LOGGER.info("Failed to update topics of interest : " + e.getMessage());
            response.sendRedirect(errorPageJspView);
        }
    }
}
