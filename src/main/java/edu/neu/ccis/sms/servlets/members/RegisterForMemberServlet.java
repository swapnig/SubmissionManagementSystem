package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.JspViews;

/**
 * Register for an active registerable member, for a specific role,
 * 
 * <br><br>Retrieves data asynchronously from following api-urls
 * <br>1. "/ReadMembers"  : Read all active members for given category and parent member
 * <br>2. "/ReadMemberDetails" : Read details of the member, user is trying to register
 * 
 * <br><br>Submits data asynchronously to following api-urls
 * <br>3. "/RegisterUserForMember" : Register user for the selected member
 * 
 * @author Swapnil Gupta
 * @date 10-Jun-2015
 * @lastUpdate 20-June-2015
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
     * Register for an active registerable member, for a specific role, if the user does not already
     * have that role for the member
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(JspViews.REGISTER_FOR_MEMBER_VIEW).forward(request, response);
    }
}
