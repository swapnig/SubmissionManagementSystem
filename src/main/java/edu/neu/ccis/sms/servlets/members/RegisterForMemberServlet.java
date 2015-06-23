package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.JspViews;

/**
 * Servlet for Register for a member request, currently just forwards the request to the
 * {@link edu.neu.ccis.sms.constants.JspViews#REGISTER_FOR_MEMBER_VIEW}.
 * 
 * @author Swapnil Gupta
 * @since Jun 10, 2015
 * @version SMS 1.0
 *
 */
@WebServlet("/RegisterForMember")
public class RegisterForMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterForMemberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(JspViews.REGISTER_FOR_MEMBER_VIEW).forward(request, response);
    }
}
