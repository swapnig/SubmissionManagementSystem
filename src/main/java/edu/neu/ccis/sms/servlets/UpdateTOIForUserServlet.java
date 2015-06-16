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

            Set<String> toiSet = one.getTopicsOfInterest();
            // Remove the old topics of preferences
            toiSet.clear();
            userDao.updateUser(one);

            Enumeration<String> paramNames = request.getParameterNames();
            toiSet = new HashSet<String>();
            while (paramNames.hasMoreElements()) {
                String topic = paramNames.nextElement();
                toiSet.add(topic);
            }

            // Update the new topics of interest
            one.setTopicsOfInterest(toiSet);
            userDao.updateUser(one);
        } catch (final Exception e) {
            e.printStackTrace();

        }
    }
}
