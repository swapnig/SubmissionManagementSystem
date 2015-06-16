package edu.neu.ccis.sms.servlets.members.registerable;

import java.io.IOException;
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
import edu.neu.ccis.sms.entity.users.RoleType;
/**
 * Servlet implementation class ViewRegisterableMemberServlet
 */
@WebServlet("/ViewRegistrableMember")
public class ViewRegistrableMemberServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ViewRegistrableMemberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String memberId = request.getParameter(RequestKeys.PARAM_MEMBER_ID);
		MemberDao memberDao = new MemberDaoImpl();
		
		UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();
		Long userId = (Long) request.getSession().getAttribute(SessionKeys.keyUserId);
		Member member = memberDao.getMember(Long.parseLong(memberId));
		
		if (userToMemberMappingDao.doesUserHaveRoleForMember(userId, RoleType.CONDUCTOR, member.getId())) {
			request.setAttribute(RequestKeys.PARAM_ROLE_CONDUCTOR, true);
		}
		
		Set<MemberAttribute> memberAttributes = member.getAttributes();
		
		request.setAttribute(RequestKeys.PARAM_MEMBER_ID, memberId);
		request.setAttribute(RequestKeys.PARAM_MEMBER_NAMEE, member.getName());
		request.setAttribute(RequestKeys.PARAM_MEMBER_ATTRIBUTES, memberAttributes);
		request.getRequestDispatcher(JspViews.VIEW_REGISTRABLE_MEMBER_VIEW).forward(request, response);
		
		HttpSession session = request.getSession(true);
		session.setAttribute(SessionKeys.activeMemberId, memberId);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
