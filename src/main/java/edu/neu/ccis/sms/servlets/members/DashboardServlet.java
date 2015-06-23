package edu.neu.ccis.sms.servlets.members;

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
 * Show all the active registrations for the user on their dashboard, arranged by the roles user has on
 * the members.
 * 
 * @author Swapnil Gupta
 * @createdOn Jun 1, 2015
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
     * When the request is received get all the active registration for the current user,
     * and arranged by the role user has for the member. highest role first
     * 
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Get the generic role from the terminology specific role, using the mapping specified in the
         * terminology, which is stored as a key value pair in a map within the servlet context
         */
        ServletContext context = getServletContext();
        @SuppressWarnings("unchecked")
        HashMap<String, String> roleKeyToRoles =
        (HashMap<String, String>) context.getAttribute(ContextKeys.ROLE_KEY_TO_ROLE);

        // Get the current user from seesion
        Long userId = (Long) request.getSession(false).getAttribute(SessionKeys.keyUserId);
        String userName = (String) request.getSession(false).getAttribute(SessionKeys.keyUserName);

        // Get active registered members for the user
        LinkedHashMap<String, ArrayList<Member>> rolesToMembers = getActiveRegisteredMemberForUser(userId, roleKeyToRoles);

        // Set attributes in request scope
        request.setAttribute("rolesToMembers", rolesToMembers);
        request.setAttribute("userName", userName);

        request.getRequestDispatcher(JspViews.DASHBOARD_VIEW).forward(request, response);
    }

    /**
     * For each role available in the application get all the active registrations for the user.
     * Arrange each member by the role user has for the member, Members on which user has highest level of
     * access comes first.
     * 
     * @param request request object for current servlet
     */
    private LinkedHashMap<String, ArrayList<Member>> getActiveRegisteredMemberForUser(final Long userId,
            final HashMap<String, String> roleKeyToRoles) {

        /*
         * Get all the registrable members, the user is registered for
         */
        UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();
        List<UserToMemberMapping> userToMemberMappings =
                userToMemberMappingDao.getAllRegisterableMembersForUser(userId);

        /*
         * Arrange all the registrables members, the user is registered for by the role user
         * has on that member.
         */
        LinkedHashMap<String, ArrayList<Member>> rolesToMembers = new LinkedHashMap<String, ArrayList<Member>>();
        ArrayList<Member> members = new ArrayList<Member>();

        /*
         * Starting from the conductor role, for each role available in the application arrange active members
         * by the role user has on that member
         */
        RoleType currentRole = RoleType.CONDUCTOR;
        for (UserToMemberMapping userToMemberMapping : userToMemberMappings) {

            /*
             * If new role is encountered, sort all the members in the previous role by their name,
             * and store then in a map as a list value, for the role key
             */
            if (currentRole != userToMemberMapping.getRole()) {
                String roleName = currentRole.toString().toLowerCase();
                Collections.sort(members, new MemberComparator());
                rolesToMembers.put(roleKeyToRoles.get(roleName), members);

                /*
                 * Update the current role to the new role and initialize an empty member list for this new role
                 */
                members = new ArrayList<Member>();
                currentRole = userToMemberMapping.getRole();
            }

            /*
             * For each member check whether this member is currently active and add it to the list
             * of members, the user has current role
             */
            Member member = userToMemberMapping.getMember();
            if (member.isRegisterable() && member.getActivationStatus() == MemberStatusType.ACTIVE) {
                members.add(member);
            }
        }

        /*
         * Add the last role and its members, if there is at least one member on which user has this role
         */
        if(CollectionUtils.isNotEmpty(members)) {
            Collections.sort(members, new MemberComparator());
            String roleName = currentRole.toString().toLowerCase();
            rolesToMembers.put(roleKeyToRoles.get(roleName), members);
        }
        return rolesToMembers;
    }

}
