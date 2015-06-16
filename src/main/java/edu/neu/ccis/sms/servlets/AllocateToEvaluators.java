package edu.neu.ccis.sms.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.dao.users.UserToReviewerMappingDao;
import edu.neu.ccis.sms.dao.users.UserToReviewerMappingDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.submissions.Document;
import edu.neu.ccis.sms.entity.users.User;
import edu.neu.ccis.sms.entity.users.UserToReviewerMapping;

/**
 * Servlet implementation class AllocateToEvaluators
 * 
 * @author Pramod R. Khare
 * @date 2-June-2015
 * @lastUpdate 8-June-2015
 */
@WebServlet(name = "AllocateToEvaluators", urlPatterns = { "/AllocateToEvaluators" })
@MultipartConfig
public class AllocateToEvaluators extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public AllocateToEvaluators() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Upon receiving file upload submission, parses the request to read upload
     * data and saves the file on disk.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        try {
            HttpSession session = request.getSession(false);
            Long userId = (Long) session.getAttribute(SessionKeys.keyUserId);

            Long activeMemberId = (Long) session.getAttribute(SessionKeys.activeMemberId);
            activeMemberId = new Long(2);
            // TODO Validate if he is the conductor for given active member id

            // Get request parameter values: memberId and
            // numberOfEvaluatorsPerSub
            Long memberId = Long.parseLong(request.getParameter("memberId"));
            int numberOfEvaluatorsPerSubmission = Integer.parseInt(request.getParameter("numberOfEvaluatorsPerSub"));

            // DAO impls
            UserToReviewerMappingDao userToRevDao = new UserToReviewerMappingDaoImpl();
            MemberDao memberDao = new MemberDaoImpl();

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

            // redirects client to message page
            response.sendRedirect("pages/success.jsp");
        } catch (Exception ex) {
            request.setAttribute("message", "There was an error: " + ex.getMessage());
            // redirects client to message page
            System.out.println(ex.getMessage());
            response.sendRedirect("pages/error.jsp");
        }
    }

    /**
     * Takes a collection of documents submitted , and a user object - to find
     * of there is any submission document submitted by this user
     * 
     * @param submisions
     * @param submitter
     * @return Document object if is it submitted by given user else returns
     *         null
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
