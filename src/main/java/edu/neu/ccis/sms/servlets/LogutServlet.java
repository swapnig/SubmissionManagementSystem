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
 * Servlet implementation class LogutServlet
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
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
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
