package edu.neu.ccis.sms.servlets.members;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.neu.ccis.sms.constants.ErrorMessageKeys;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberStatusType;

/**
 * Toggle activation status (active/inactive) for a member and enforce parent's new activation state on all its children.
 * With restriction that member's parent need to be active for that member to be active.
 * 
 * Authentication of user for performing this action is done from the front end.
 * 
 * @author Swapnil Gupta
 * @createdOn June 18, 2015
 *
 */
@WebServlet("/ToggleMemberActivation")
public class ToggleMemberActivationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ToggleMemberActivationServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ToggleMemberActivationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * When the request is received toggles the current activation state of the member between active and
     * inactive state. THe new activation state of the parent is enforced on all its child members
     * 
     * i.e.
     * 1. If an attempt is made to inactivate an active member,
     *      inactivates that member and all its child members recursively.
     * 2. If an attempt is made to activate an inactive member,
     *      activate that member and all its child members recursively,
     *      If the member's parent is in already active,
     *      Else show error message indicating that parent member needs to be active to activate the child member
     * 
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

        // Get the member id from request parameters
        String memberIdParam = request.getParameter(RequestKeys.PARAM_MEMBER_ID);
        Long memberId = new Long(memberIdParam);
        MemberDao memberDao = new MemberDaoImpl();
        Member member = memberDao.getMember(memberId);

        // Response data from the server
        StringBuffer responseData = new StringBuffer();

        /*
         * Toggling the activations state of the member, involves updating a member.
         * The action to update an member can throw an exception while alfresco tries to
         * rename a member and update its CMS path
         */
        try {

            /*
             * Since we intend to toggle the activation state of the member, we check for the
             * current activation state of the member and then take steps to toggle its state
             */
            if(member.getActivationStatus() == MemberStatusType.ACTIVE) {

                // Update member activation state to inactive
                member.setActivationStatus(MemberStatusType.INACTIVE);
                memberDao.updateMember(member);

                /*
                 * If we make a member inactive we also want all its child members, any level down the
                 * hierarchy to also become inactive, so we recursively trace child members for the member and
                 * set them inactive one by one.
                 * i.e. If a department is inactive all its courses become inactive
                 */
                memberDao.changeChildMemberActivationStatusByParentMemberId(memberId, MemberStatusType.INACTIVE);
                responseData.append(member.getName() +  " and all its children are now inactive");
                LOGGER.info("Member " + memberId +  " and all its children are now inactive");
            } else {

                Member parentMember = member.getParentMember();

                /*
                 * While changing the activation state of a member from inactive to active, we want to check
                 * whether the parent of that member is currently in active state, this is necessary to
                 * ensure the consistency that a child member of a inactive parent cannot itself be active.
                 * i.e we cannot have an active course for an inactive department
                 * 
                 * If the parent is active we set the member and all its child members to active state,
                 * else we display an error message indicating that user needs to activate the parent first
                 */
                if (parentMember.getActivationStatus() == MemberStatusType.ACTIVE) {

                    // Update member activation state to active
                    member.setActivationStatus(MemberStatusType.ACTIVE);
                    memberDao.updateMember(member);

                    /*
                     * If we make a member active we also want all its child members, any level down the
                     * hierarchy to also become active, so we recursively trace child members for the member and
                     * set them active one by one.
                     * i.e. If a department is active all its courses become active.
                     * 
                     * If we need a course within the department to be inactive we need to explicitly
                     * perform that action
                     */
                    memberDao.changeChildMemberActivationStatusByParentMemberId(memberId, MemberStatusType.ACTIVE);
                    responseData.append(member.getName() +  " and all its children are now active");
                    LOGGER.info("Member " + memberId +  " and all its children are now active");
                } else {
                    responseData.append("<font size='4' color='red'>" + parentMember.getName() + " : "
                            + ErrorMessageKeys.PARENT_MEMBER_INACTIVE + "</font>");
                    LOGGER.info("Attempt to activate member: " + memberId + " failed with error "
                            + ErrorMessageKeys.PARENT_MEMBER_INACTIVE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("Unable to toggle activation state for member: " + memberId);
        }

        response.setContentType("text/html");
        response.getWriter().write(responseData.toString());
    }
}
