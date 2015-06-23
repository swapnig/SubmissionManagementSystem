package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;

/**
 * Show view only details for a submittable member, along with user role specific actions
 * 
 * @author Swapnil Gupta
 * @createdOn Jun 11, 2015
 *
 */
@WebServlet("/ViewSubmittableMember")
public class ViewSubmittableMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ViewSubmittableMemberServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewSubmittableMemberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * When the request is received to view a submittable, show the view only details to user.
     * Also identifies the user role for the member and provide option to perform activities authorized for their role
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

        // Get all the attributes of the member as a list sorted by their id
        Set<MemberAttribute> memberAttributesSet = member.getAttributes();
        ArrayList<MemberAttribute> membersAttributes = new ArrayList<MemberAttribute> (memberAttributesSet);
        Collections.sort(membersAttributes);

        /*
         * Get the registrable member id for the submittable member from user session
         * Set the submittable member id as active submittable member id in the user session,
         * to track the submittable member the user is currently working with
         */
        HttpSession session = request.getSession(false);
        Long registrableParentMemberId = (Long) session.getAttribute(SessionKeys.activeMemberId);
        session.setAttribute(SessionKeys.activeSubmittableMemberId, activeMemberId);

        LOGGER.info("User id: " + userId + " accessed its submittable member " + memberId);

        /*
         * For the given submittable member check what role (level of access) user has on its registrable
         * parent member. This is stored in the request scope, which is in turn used by jsp view,
         * to show functionalities specific to a given role
         */
        List<UserToMemberMapping> mappings = userToMemberMappingDao.getAllUserRolesForMember(userId, registrableParentMemberId);
        for (UserToMemberMapping mapping : mappings) {
            request.setAttribute(mapping.getRole().toString(), true);
        }

        // Set attributes in request scope
        request.setAttribute(RequestKeys.PARAM_MEMBER_ID, memberId);
        request.setAttribute(RequestKeys.PARAM_MEMBER_NAME, member.getName());
        request.setAttribute(RequestKeys.PARAM_MEMBER_ATTRIBUTES, membersAttributes);

        request.getRequestDispatcher(JspViews.VIEW_SUBMITTABLE_MEMBER_VIEW).forward(request, response);
    }

}
