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
 * Servlet implementation class UpdateTOIForUserServlet
 * 
 * @author Pramod R. Khare
 * @date 2-June-2015
 * @lastUpdate 8-June-2015
 */
@WebServlet(name = "UpdateTOIForUserServlet", urlPatterns = { "/UpdateTOIForUser" })
public class UpdateTOIForUserServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateTOIForUserServlet() {
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
            response.sendRedirect("pages/update_toi.jsp");
        } catch (final Exception e) {
            e.printStackTrace();
            response.sendRedirect("pages/error.jsp");
        }
    }
}
