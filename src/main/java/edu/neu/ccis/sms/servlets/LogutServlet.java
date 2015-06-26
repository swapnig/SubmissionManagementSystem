package edu.neu.ccis.sms.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LogutServlet; Invalidates the current logged-in user's session and forward the user back
 * to login sceen
 * 
 * @author Pramod R. Khare
 * @date 28-May-2015
 * @lastUpdate 1-June-2015
 */
@WebServlet("/Logout")
public class LogutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(LogutServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LogutServlet() {
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
     * Invalidate current logged-in user's http session and forward him/her back to login screen
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        LOGGER.info("Method - LogutServlet:doPost");

        HttpSession session = request.getSession(false);
        if (session != null) {
            LOGGER.info("Session invalidated! User successfully logged out!");
            session.invalidate();
            session = null;
        }

        response.sendRedirect("pages/login.jsp");
    }
}
