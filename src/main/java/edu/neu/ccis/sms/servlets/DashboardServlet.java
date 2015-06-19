package edu.neu.ccis.sms.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;

import edu.neu.ccis.sms.comparator.MemberComparator;
import edu.neu.ccis.sms.constants.ContextKeys;
import edu.neu.ccis.sms.constants.JspViews;
import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberStatusType;
import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.users.RoleType;

/**
 * Servlet implementation class Dashboard View
 * @author Swapnil Gupta
 * @date June 9, 2015
 * @lastUpdate Jun 11, 2015
 *
 */
@WebServlet("/Dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DashboardServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        ServletContext context = getServletContext();
        @SuppressWarnings("unchecked")
        HashMap<String, String> roleKeyToRoles =
        (HashMap<String, String>) context.getAttribute(ContextKeys.ROLE_KEY_TO_ROLE);

        Long userId = (Long) request.getSession(false).getAttribute(SessionKeys.keyUserId);
        String userName = (String) request.getSession(false).getAttribute(SessionKeys.keyUserName);
        LinkedHashMap<String, ArrayList<Member>> rolesToMembers = getActiveRegisteredMemberForUser(userId, roleKeyToRoles);

        request.setAttribute("rolesToMembers", rolesToMembers);
        request.setAttribute("userName", userName);
        request.getRequestDispatcher(JspViews.DASHBOARD_VIEW).forward(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
    }

    /**
     * Set configurations parameters in Http request
     * @param request request object for current servlet
     */
    private LinkedHashMap<String, ArrayList<Member>> getActiveRegisteredMemberForUser(final Long userId,
            final HashMap<String, String> roleKeyToRoles) {
        UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();
        List<UserToMemberMapping> userToMemberMappings = userToMemberMappingDao.getAllRegisterableMembersForUser(userId);

        RoleType currentRole = RoleType.CONDUCTOR;
        LinkedHashMap<String, ArrayList<Member>> rolesToMembers = new LinkedHashMap<String, ArrayList<Member>>();
        ArrayList<Member> members = new ArrayList<Member>();
        for (UserToMemberMapping userToMemberMapping : userToMemberMappings) {
            if (currentRole != userToMemberMapping.getRole()) {
                String roleName = currentRole.toString().toLowerCase();
                Collections.sort(members, new MemberComparator());
                rolesToMembers.put(roleKeyToRoles.get(roleName), members);
                members = new ArrayList<Member>();
                currentRole = userToMemberMapping.getRole();
            }
            Member member = userToMemberMapping.getMember();
            if (member.isRegisterable() && member.getActivationStatus() == MemberStatusType.ACTIVE) {
                members.add(member);
            }
        }
        if(CollectionUtils.isNotEmpty(members)) {
            Collections.sort(members, new MemberComparator());
            String roleName = currentRole.toString().toLowerCase();
            rolesToMembers.put(roleKeyToRoles.get(roleName), members);
        }
        return rolesToMembers;
    }

}
