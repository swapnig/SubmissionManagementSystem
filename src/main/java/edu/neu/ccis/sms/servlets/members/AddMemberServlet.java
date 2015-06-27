package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.JspViews;

/**
 * Add new member; addition of new members is hierarchy based; Members can only be added as child members
 * of existing members.
 * 
 * <br><br>Retrieves data asynchronously from following api-urls
 * <br>1. "/ReadActiveMembers"  : Read all active members for given category and parent member
 * <br>2. "/CreateNewMemberForm" : Create New member form
 * 
 * <br><br>Submits data asynchronously to following api-urls
 * <br>1. "/CreateMember" : Persist new member in database
 * 
 * @author Swapnil Gupta
 * @date 24-May-2015
 * @lastUpdate 20-June-2015
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
     * Handle the get request, for the web servlet, redirecting to jsp
     * {@link edu.neu.ccis.sms.constants.JspViews#ADD_MEMBER_VIEW}.
     * 
     * Addition of new members is hierarchy based. You can only add members as child members
     * to existing members.
     *
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(JspViews.ADD_MEMBER_VIEW).forward(request, response);
    }
}
