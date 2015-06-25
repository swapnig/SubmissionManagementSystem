package edu.neu.ccis.sms.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
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
import edu.neu.ccis.sms.dao.users.UserToReviewerMappingDao;
import edu.neu.ccis.sms.dao.users.UserToReviewerMappingDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.submissions.Document;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;
import edu.neu.ccis.sms.entity.users.UserToReviewerMapping;

/**
 * Servlet implementation class AllocateToEvaluators; This servlet serves requests for dynamic allocation of
 * evaluators/reviewer for submissions from submitters.
 * 
 * This servlet takes two request parameters,
 * 
 * 1) memberId - submittable member for which reviewers to be allocated
 * 
 * 2) numberOfEvaluatorsPerSub - number of reviewers/evaluators to allocate per submission for this submittable member;
 * if the value is more than actually available number of evaluators, then allocates only available number of evaluators
 * to submissions
 * 
 * @author Pramod R. Khare
 * @date 2-June-2015
 * @lastUpdate 8-June-2015
 */
@WebServlet(name = "AllocateToEvaluators", urlPatterns = { "/AllocateToEvaluators" })
@MultipartConfig
public class AllocateToEvaluators extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AllocateToEvaluators.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AllocateToEvaluators() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * This servlet allocates evaluators to grade the submissions; and it takes two request parameters,
     * 
     * 1) memberId - submittable member for which reviewers to be allocated
     * 
     * 2) numberOfEvaluatorsPerSub - number of reviewers/evaluators to allocate per submission for this submittable
     * member; if the value is more than actually available number of evaluators, then allocates only available number
     * of evaluators to submissions
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        LOGGER.info("Method - AllocateToEvaluators:doPost");
        try {
            // DAO impls
            UserToReviewerMappingDao userToRevDao = new UserToReviewerMappingDaoImpl();
            MemberDao memberDao = new MemberDaoImpl();
            UserToMemberMappingDao user2MemberMapDao = new UserToMemberMappingDaoImpl();

            HttpSession session = request.getSession(false);
            Long userId = (Long) session.getAttribute(SessionKeys.keyUserId);
            Long activeMemberId = (Long) session.getAttribute(SessionKeys.activeMemberId);

            // Important TODO Validate if he is the conductor for given active member id
            List<UserToMemberMapping> rolesMapping = user2MemberMapDao.getAllUserRolesForMember(userId, activeMemberId);
            boolean isConductorRole = false;
            for (UserToMemberMapping mapping : rolesMapping) {
                if (mapping.getRole() == RoleType.CONDUCTOR) {
                    isConductorRole = true;
                    break;
                }
            }

            // If User is not a Conductor for this Member, then Show Error
            if (!isConductorRole) {
                LOGGER.info("User is not conductor on given registerable member, Cannot access this page!");
                request.setAttribute(RequestKeys.PARAM_MESSAGE, "Invalid page access!");
                getServletContext().getRequestDispatcher(JspViews.ERROR_PAGE_VIEW).forward(request, response);
                return;
            }

            // TODO change it to take the current submittable member rather than a drop down
            Long memberId = (Long) session.getAttribute(SessionKeys.activeSubmittableMemberId);

            // Get request parameter values: memberId and numberOfEvaluatorsPerSub
            memberId = Long.parseLong(request.getParameter("memberId"));
            int numberOfEvaluatorsPerSubmission = Integer.parseInt(request.getParameter("numberOfEvaluatorsPerSub"));

            // TODO In case of, "Allocate Evaluators" is clicked more than one
            // times, then before doing any new allocation for evaluators,
            // delete any previous old allocations and allocate anew
            userToRevDao.deleteAllUserToReviewerMappingsForMember(memberId);

            List<User> evaluatorsList = new ArrayList<User>(memberDao.getEvaluatorsForMemberId(activeMemberId));
            List<User> submittersList = new ArrayList<User>(memberDao.getSubmittersForMemberId(activeMemberId));
            Set<Member> allSubmittableMembers = memberDao.findAllSubmittableMembersByParentMemberId(activeMemberId);
            Member submittableToAllocateFor = memberDao.getMemberByIdWithSubmissions(memberId);
            Set<Document> submisions = submittableToAllocateFor.getSubmissions();

            // Sort by MemberId to get the index number of this submittable
            // member - for example, there are 10 assignments under a course,
            // then for each assignment we should try to allocate different
            // reviewers or evaluators for each submitters, but its not
            // mandatory, its an optimally random way
            List<Member> submittableList = new ArrayList<Member>(allSubmittableMembers);
            Collections.sort(submittableList);
            int submittableMemberPosition = submittableList.indexOf(submittableToAllocateFor);

            // Check if there are enough evaluators to allocate from when
            // numberOfEvaluatorsPerSub is greater than the total available
            numberOfEvaluatorsPerSubmission = (numberOfEvaluatorsPerSubmission >= evaluatorsList.size()) ? evaluatorsList
                    .size() : numberOfEvaluatorsPerSubmission;

            int totalNoOfEvaluators = evaluatorsList.size();
            // Allocate the reviewer or evaluator for each Submitter user and
            // save their mapping into the database
            for (int i = 0; i < submittersList.size(); i++) {
                // Generate the evalUserIndex for numberOfEvaluatorsPerSub times
                // i.e. When there are multiple reviewers to allocate per
                // submission for given member, then pick the nearly-randomized
                // Evluator User, and keep in a Set, continue choosing the
                // evaluator randomly until all required users found
                Set<User> allocatedEvaluators = new HashSet<User>();
                int allocatedCount = 0;
                while (allocatedCount < numberOfEvaluatorsPerSubmission) {
                    int evalUserIndex = (i + submittableMemberPosition + allocatedCount) % totalNoOfEvaluators;
                    User choosenEvaluator = evaluatorsList.get(evalUserIndex);
                    if (allocatedEvaluators.add(choosenEvaluator)) {
                        allocatedCount++;
                    }
                }

                User submitter = submittersList.get(i);

                for (User allocatedEvaluator : allocatedEvaluators) {
                    // save this user to evaluator for given member id mapping
                    // into database
                    UserToReviewerMapping mapping = new UserToReviewerMapping();
                    mapping.setEvaluator(allocatedEvaluator);
                    mapping.setSubmitter(submitter);
                    mapping.setEvaluationForMemberId(submittableToAllocateFor.getId());

                    // Get the document id for this user's submission for this
                    // member - if user has not submitted yet for this
                    // submittable
                    // member, then leave the field as null
                    Document submissionDocBySubmitter = findSubmissionDocumentForUserFromAllSubmissions(submisions,
                            submitter);
                    if (null != submissionDocBySubmitter) {
                        mapping.setEvaluateDocId(submissionDocBySubmitter.getId());
                    }
                    userToRevDao.saveUserToReviewerMapping(mapping);
                }
            }

            LOGGER.info("Allocating Evaluators to Submitters done successfully!");
            request.setAttribute(RequestKeys.PARAM_MESSAGE, "Successfully allocated evaluators to submissions!");
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute(RequestKeys.PARAM_MESSAGE,
                    "Failed to allocate evaluators for submissions. Please retry or contact administrator.");
            LOGGER.info("Unable to allocate evaluators : " + ex.getMessage());
        }
        request.getRequestDispatcher(JspViews.ALLOCATE_EVALUATORS_VIEW).forward(request, response);
    }

    /**
     * Takes a collection of documents submitted , and a user object - to find of there is any submission document
     * submitted by this user
     * 
     * @param submisions
     *            - list of documents submitted for this submittable member
     * @param submitter
     *            - A user for whom we are checking if he/she has submitted any document in above list of documents
     * @return Document object if is it submitted by given user else returns null
     */
    private Document findSubmissionDocumentForUserFromAllSubmissions(Set<Document> submisions, User submitter) {
        for (Document doc : submisions) {
            if (doc.getSubmittedBy().contains(submitter)) {
                return doc;
            }
        }
        return null;
    }
}
