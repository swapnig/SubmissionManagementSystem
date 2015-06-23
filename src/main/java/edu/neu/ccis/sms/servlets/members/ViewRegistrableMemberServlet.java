package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.neu.ccis.sms.constants.JspViews;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberAttribute;
import edu.neu.ccis.sms.entity.users.RoleType;
/**
 * Show view only details for a registrable member, along with option to view submittables.
 * If the user has a conductor access for the registrable member then provide option to edit member details and
 * add roles for other user through their email
 * 
 * @author Swapnil Gupta
 * @createdOn Jun 8, 2015
 *
 */
@WebServlet("/ViewRegistrableMember")
public class ViewRegistrableMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ViewRegistrableMemberServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewRegistrableMemberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * When the request is received get the details of the registrable member, providing option to view
     * all the child submittables for this member.
     * 
     * If the current user has conductor access on the registrable member, provide option
     * to edit details, add role for user by email.
     * 
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Get the member id from the request parameters and set it to the active member id
         * Active member id is stored in the user session, to identify the member on which the user
         * is currently working.
         */
        String memberId = request.getParameter(RequestKeys.PARAM_MEMBER_ID);
        Long activeMemberId = Long.parseLong(memberId);
        MemberDao memberDao = new MemberDaoImpl();
        Member member = memberDao.getMember(activeMemberId);

        // Get the current user from session
        UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();
        Long userId = (Long) request.getSession(false).getAttribute(SessionKeys.keyUserId);

        /*
         * Check whether this user has conductor access on the current member
         * This is required as we use this in the jsp view to show functionaliies only intended for the conductors
         */
        if (userToMemberMappingDao.doesUserHaveRoleForMember(userId, RoleType.CONDUCTOR, member.getId())) {
            request.setAttribute(RequestKeys.PARAM_ROLE_CONDUCTOR, true);
        }

        LOGGER.info("User id: " + userId + " accessed its registrable member " + memberId);

        // Get all the attributes of the member as a list sorted by their id
        Set<MemberAttribute> memberAttributesSet = member.getAttributes();
        ArrayList<MemberAttribute> membersAttributes = new ArrayList<MemberAttribute> (memberAttributesSet);
        Collections.sort(membersAttributes);

        // Add attributes in the request scope
        request.setAttribute(RequestKeys.PARAM_MEMBER_ID, memberId);
        request.setAttribute(RequestKeys.PARAM_MEMBER_NAME, member.getName());
        request.setAttribute(RequestKeys.PARAM_MEMBER_ATTRIBUTES, membersAttributes);
        request.getRequestDispatcher(JspViews.VIEW_REGISTRABLE_MEMBER_VIEW).forward(request, response);

        // Store the active member id in seesion
        HttpSession session = request.getSession(false);
        session.setAttribute(SessionKeys.activeMemberId, activeMemberId);
    }
}
