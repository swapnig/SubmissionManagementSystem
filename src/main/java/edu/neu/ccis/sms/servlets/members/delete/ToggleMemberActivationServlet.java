package edu.neu.ccis.sms.servlets.members.delete;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberStatusType;

/**
 * Servlet implementation class DeleteMemberServlet
 */
@WebServlet("/ToggleMemberActivation")
public class ToggleMemberActivationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ToggleMemberActivationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String memberIdParam = request.getParameter(RequestKeys.PARAM_MEMBER_ID);
        Long memberId = new Long(memberIdParam);
        MemberDao memberDao = new MemberDaoImpl();
        Member member = memberDao.getMember(memberId);

        StringBuffer content = new StringBuffer();
        try {
            if(member.getActivationStatus() == MemberStatusType.ACTIVE) {
                member.setActivationStatus(MemberStatusType.INACTIVE);
                memberDao.updateMember(member);
                memberDao.changeChildMemberActivationStatusByParentMemberId(memberId, MemberStatusType.INACTIVE);
                content.append(member.getName() +  " and all its children are now inactive");
            } else {
                Member parentMember = member.getParentMember();
                if (parentMember.getActivationStatus() == MemberStatusType.ACTIVE) {
                    member.setActivationStatus(MemberStatusType.ACTIVE);
                    memberDao.updateMember(member);
                    memberDao.changeChildMemberActivationStatusByParentMemberId(memberId, MemberStatusType.ACTIVE);
                    content.append(member.getName() +  " and all its children are now active");
                } else {
                    content.append("<font size='4' color='red'>" + member.getName() + "'s parent " + parentMember.getName() + " is  inactive, activate it first.</font>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setContentType("text/html");
        response.getWriter().write(content.toString());
    }

}
