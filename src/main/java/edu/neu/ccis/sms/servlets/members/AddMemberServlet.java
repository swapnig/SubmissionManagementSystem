package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.JspViews;

/**
 * Servlet for Add Member request, currently just forwards the request to the
 * {@link edu.neu.ccis.sms.constants.JspViews#ADD_MEMBER_VIEW}.
 * 
 * @author Swapnil Gupta
 * @since May 25, 2015
 * @version SMS 1.0

 * 
 * @jsp {@link edu.neu.ccis.sms.constants.JspViews#ADD_MEMBER_VIEW}
 * Displays the user interface to add new member. Addition of new members is hierarchy based.
 * You can only add members as child members to existing members, or create a new parent member
 * first and then the child member
 * 
 * Further mapping includes ajax calls through javascript to
 * "/ReadMembers"  : Read all the members
 * "/CreateNewMemberForm" : Create New member form
 * "/CreateMember" : Persist new member in database
 *
 */
@WebServlet("/AddMember")
public class AddMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddMemberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Handle the get request, for the web servlet, currently just redirects to jsp
     * {@link edu.neu.ccis.sms.constants.JspViews#ADD_MEMBER_VIEW}
     *
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(JspViews.ADD_MEMBER_VIEW).forward(request, response);
    }
}
