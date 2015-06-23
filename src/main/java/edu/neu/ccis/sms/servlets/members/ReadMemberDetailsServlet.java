package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.RegexPattern;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberAttribute;
import edu.neu.ccis.sms.entity.categories.MemberStatusType;
import edu.neu.ccis.sms.entity.users.RoleType;

/**
 * Read all the attributes for the current member providing view only access, for conductors provide
 * additional functionality to edit/update member attributes, add role for user on member and
 * activate/ inactivate a member
 * 
 * @author Swapnil Gupta
 * @createdOn May 28, 2015
 *
 */
@WebServlet("/ReadMemberDetails")
public class ReadMemberDetailsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ReadMemberDetailsServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReadMemberDetailsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * On receiving the request get all the attribute for the current member, and responds back with member
     * details as a view-only form. If the user is conductor provide controls to edit, update member,
     * add roles for user on member and activate/Inactivate a member.
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        /*
         * Get the parent member id from request parameters, this is used to get all its attributes.
         */
        String memberId = request.getParameter(RequestKeys.PARAM_MEMBER_ID);
        MemberDao memberDao = new MemberDaoImpl();
        Member member = memberDao.getMember(Long.parseLong(memberId));

        /*
         * All the member attributes are sorted by their id and hence by the sequence they were inserted
         * in the back-end. This sequencing is important as a conductor can edit these details and hence update
         * the member name, the first attribute for the member
         */
        ArrayList<MemberAttribute> membersAttributes = new ArrayList<MemberAttribute> (member.getAttributes());
        Collections.sort(membersAttributes);

        // Get the userId from session, of the user who is trying to create this member.
        UserToMemberMappingDao userToMemberMappingDao = new UserToMemberMappingDaoImpl();
        Long userId = (Long) request.getSession(false).getAttribute(SessionKeys.keyUserId);

        // Response data from the server
        StringBuffer responseData = new StringBuffer();

        // For each member attribute add a disabled input text field, since this is a view only form
        int index = 0;
        for (MemberAttribute memberAttribute : membersAttributes) {
            String attributeName = memberAttribute.getName();
            String attributeValue = memberAttribute.getValue();
            if(index++ == 0) {
                responseData.append("<tr><td>"
                        + "<label for='" + attributeName + "'>" + attributeName + "</label>"
                        + "</td>");

                responseData.append("<td>"
                        + "<input name='" + attributeName + "'"
                        + "type='text' "
                        + "value='" + attributeValue + "' "
                        + "pattern='" + RegexPattern.ALPHANUM_UNDERSCORE_HYPHEN_SPACE + "' "
                        + "required disabled/> (only alphanumeric, underscore, hyphen)"
                        + "</td></tr>");
            } else {
                responseData.append("<tr><td>"
                        + "<label for='" + attributeName + "'>" + attributeName + "</label></td>");

                responseData.append("<td>"
                        + "<textarea name='" + attributeName + "' disabled>" + attributeValue + "</textarea>"
                        + "</td></tr>");
            }
        }

        LOGGER.info("Listed details for member: " + memberId);

        /*
         * Add additional functionalities for the user with conductor role on the member
         * 1. Update a member
         * 2. Add a role for a user(identified by email id) for the given member
         * 3. Activate/Inactivate a member
         * 
         * All these functionalities are unavailable on page load and only become available when conductor
         * clicks on the edit member button, this is handled on the front-end
         */
        if (userToMemberMappingDao.doesUserHaveRoleForMember(userId, RoleType.CONDUCTOR, member.getId())) {
            responseData.append("<tr><td colspan='2'><input id='editMemberAttributes' type='button' value='Edit'/>");
            responseData.append("<input type='submit' id='saveMemberAttributes' value='Update' style='display:none'/>");

            // Check the current activation state of the selected member, and provide button to toggle activation status.
            if(member.getActivationStatus() == MemberStatusType.ACTIVE) {
                responseData.append("<input type='button' id='toggleMemberActivation' value='Inactivate' "
                        + "style='display:none'/></td></tr>");
            } else {
                responseData.append("<input type='button' id='toggleMemberActivation' value='Activate' "
                        + "style='display:none'/></td></tr>");
            }
            responseData.append("<tr><td><input type='hidden' name='memberId' value='" + memberId + "'/></td></tr>");
            LOGGER.info(userId + " having conductor priveleges, accessed the member: " + memberId);
        }

        response.setContentType("text/html");
        response.getWriter().write(responseData.toString());
    }

}
