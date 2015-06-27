package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.JspViews;

/**
 * Provide view only member details, for users with conductor access provide additional functionality
 * to edit member, add role for user to member, activate/inactivate member.
 * 
 * <br><br>Retrieves data asynchronously from following api-urls
 * <br>1. "/ReadMembers"  : Read all members for given category and parent member
 * <br>2. "/ReadMemberDetails" : Read details for an individual member
 * 
 * <br><br>Submits data asynchronously to following api-urls
 * <br>1. "/ToggleMemberActivation" : Toggle activation state of selected member
 * <br>2. "/UpdateMember" : Update selected member details
 * <br>3. "/AddRoleToMember" : Add role for another user on selected member
 * 
 * @author Swapnil Gupta
 * @date 24-May-2015
 * @lastUpdate 20-June-2015
 *
 */
@WebServlet("/ViewMemberDetails")
public class ViewMemberDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewMemberDetailsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * Provide view only member details, for users with conductor access provide additional functionality
     * to edit member, add role for user to member, activate/inactivate member.
     * 
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(JspViews.VIEW_MEMBER_DETAILS_VIEW).forward(request, response);
    }
}
