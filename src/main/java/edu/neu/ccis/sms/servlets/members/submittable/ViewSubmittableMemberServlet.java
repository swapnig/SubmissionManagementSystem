package edu.neu.ccis.sms.servlets.members.submittable;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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
 * View details for a submittable member
 * @author Swapnil Gupta
 * @since Jun 11, 2015
 * @version SMS 1.0
 *
 */
@WebServlet("/ViewSubmittableMember")
public class ViewSubmittableMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewSubmittableMemberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String memberId = request.getParameter(RequestKeys.PARAM_MEMBER_ID);
        Long activeMemberId = Long.parseLong(memberId);
        System.out.println("ViewSubmittableMember - Submittable Member id - "+activeMemberId);
        MemberDao memberDao = new MemberDaoImpl();

        UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();
        Long userId = (Long) request.getSession(false).getAttribute(SessionKeys.keyUserId);
        Member member = memberDao.getMember(activeMemberId);
        Set<MemberAttribute> memberAttributes = member.getAttributes();

        HttpSession session = request.getSession(false);
        Long registrableParentMemberId = (Long) session.getAttribute(SessionKeys.activeMemberId);
        System.out.println("ViewSubmittableMember - Submittable Member id - "+registrableParentMemberId);
        session.setAttribute(SessionKeys.activeSubmittableMemberId, activeMemberId);

        List<UserToMemberMapping> mappings = userToMemberMappingDao.getAllUserRolesForMember(userId, registrableParentMemberId);
        for (UserToMemberMapping mapping : mappings) {
            request.setAttribute(mapping.getRole().toString(), true);
        }

        request.setAttribute(RequestKeys.PARAM_MEMBER_ID, memberId);
        request.setAttribute(RequestKeys.PARAM_MEMBER_NAMEE, member.getName());
        request.setAttribute(RequestKeys.PARAM_MEMBER_ATTRIBUTES, memberAttributes);
        
        request.getRequestDispatcher(JspViews.VIEW_SUBMITTABLE_MEMBER_VIEW).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

}
