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

import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberAttribute;

/**
 * Update the new details for the member.
 * 
 * Authentication of user for performing this action is done from the front end.
 * 
 * @author Swapnil Gupta
 * @date Jun 11, 2015
 * @lastUpdate Jun 11, 2015
 *
 */
@WebServlet("/UpdateMember")
public class UpdateMemberServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(UpdateMemberServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdateMemberServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * When the request is received, update the new details for the member into the database,
     * ensuring the constraint that the new name of the member,
     * if updated does not already exist for the parent member.
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        /*
         * On the front-end it is ensured that the sequence of the member attribute fields is same as sequence
         * of category attributes in the terminology xml. Here we get all the request parameters as a list,
         * this ensures that the sequence of the member attributes would be maintained.
         * 
         * This is important as we update the value of the first member attribute as the name of member as well.
         * Which in this case becomes the first request parameter, this is ensured from the front-end
         */
        ArrayList<String> paramNames = Collections.list(request.getParameterNames());
        String memberNameParam = paramNames.get(0);
        String memberName = request.getParameter(memberNameParam);

        /*
         * Get existing member using its member id.
         */
        MemberDao memberDao = new MemberDaoImpl();
        Long memberId = Long.parseLong(request.getParameter(RequestKeys.PARAM_MEMBER_ID));
        Member existingMember = memberDao.getMember(memberId);

        // Response data from the server
        StringBuffer responseData = new StringBuffer();

        /*
         *  A member is unique for the parent if no member with the same name already exists for the parent,
         *  besides the current member, i.e. if the member name is not updated than the details should be updated.
         *  But if the member name is changed it should not be same as any other member for the parent.
         */
        boolean isUniqueMemberForParent = !existingMember.getName().equals(memberName) &&
                memberDao.doesMemberNameExistForParentMember(memberName, existingMember.getParentMember().getId());
        if (isUniqueMemberForParent) {
            responseData.append("<font size='4' color='red'>Member with same " + memberNameParam +
                    " already exists for its parent member, choose a different " + memberNameParam + ".</font>");
            LOGGER.info("Attempting to update member failed as a member with same " + memberNameParam +
                    " already exists for its parent member, choose a different name.");
        } else {

            // Update the member name
            existingMember.setName(memberName);

            /*
             * Get all the existing attributes of the member.
             * For each attribute check whether that attributes has been updated,
             * if it has been updated persist in the back-end
             */
            Set<MemberAttribute> memberAttributes = existingMember.getAttributes();
            for (MemberAttribute memberAttribute : memberAttributes) {
                String attributeName = memberAttribute.getName();
                if (paramNames.contains(attributeName)) {
                    memberAttribute.setValue(request.getParameter(attributeName));
                }
            }
            existingMember.setAttributes(memberAttributes);

            /*
             * The action to update an member can throw an exception while alfresco tries to
             * rename a member and update its CMS path, log an exception if that happens
             */
            try {
                // Update the member
                memberDao.updateMember(existingMember);

                responseData.append("Details have been updated for " + memberName);
                LOGGER.info("Updating details for member: " + memberId);
            } catch (Exception e) {
                e.printStackTrace();
                responseData.append("Failed to update the details for " + memberName);
                LOGGER.info("Exception occured while updating details for member: " + memberId);
            }
        }
        response.setContentType("text/html");
        response.getWriter().write(responseData.toString());
    }
}
