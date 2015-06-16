package edu.neu.ccis.sms.servlets;

import java.io.IOException;
import java.util.Date;
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
import edu.neu.ccis.sms.dao.submissions.DocumentDao;
import edu.neu.ccis.sms.dao.submissions.DocumentDaoImpl;
import edu.neu.ccis.sms.dao.submissions.EvaluationDao;
import edu.neu.ccis.sms.dao.submissions.EvaluationDaoImpl;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.submissions.Document;
import edu.neu.ccis.sms.entity.submissions.EvalType;
import edu.neu.ccis.sms.entity.submissions.Evaluation;
import edu.neu.ccis.sms.entity.users.User;

/**
 * Servlet implementation class UploadEvaluationsServlet
 * 
 * @author Pramod R. Khare
 * @date 2-June-2015
 * @lastUpdate 8-June-2015
 */
@WebServlet(name = "UploadEvaluationsServlet", urlPatterns = { "/UploadEvaluations" })
@MultipartConfig
public class UploadEvaluationsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadEvaluationsServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * Upon receiving file upload submission, parses the request to read upload
     * data and saves the file on disk.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        try {
            HttpSession session = request.getSession(false);
            Long evaluatorId = (Long) session.getAttribute(SessionKeys.keyUserId);

            // DAOs
            DocumentDao docDao = new DocumentDaoImpl();
            UserDao userDao = new UserDaoImpl();
            MemberDao memberDao = new MemberDaoImpl();
            EvaluationDao evalDao = new EvaluationDaoImpl();

            Long submittableMemberId = Long.parseLong(request.getParameter("submittableMemberId"));
            Float maxGrades = Float.parseFloat(request.getParameter("maxGrades"));

            User evaluatedBy = userDao.getUserByIdWithSubmittersToEvaluateMappings(evaluatorId);
            Set<User> submittersToEvaluate = evaluatedBy.getSubmittersToEvaluateForMemberId(submittableMemberId);

            Member submittableMember = memberDao.getMember(submittableMemberId);

            int count = submittersToEvaluate.size();
            // Get all the input params from request and create Evaluation
            // objects and store them into backend db
            for (int i = 0; i < count; i++) {
                String strSubmitterUserId = request.getParameter("submitterId" + i);
                try {
                    Long submitterUserId = Long.parseLong(strSubmitterUserId);
                    Float gradesReceived = Float.parseFloat(request.getParameter("gradesReceived" + i));
                    // In case gradesReceived are more than the toal maximum
                    // grades possible then
                    if (gradesReceived > maxGrades) {
                        gradesReceived = maxGrades;
                    }
                    String comments = request.getParameter("comments" + i);

                    if (!checkIfUserIsValidToEvaluate(submittersToEvaluate, submitterUserId)) {
                        System.out.println("This submitterUserId " + submitterUserId
                                + " is not supposed to be evaluated by this Evaluator!");
                        continue;
                    }

                    Document submittedDoc = userDao.getSubmissionDocumentForMemberIdByUserId(submitterUserId,
                            submittableMemberId);

                    if (submittedDoc != null) {
                        // Check if this document is already evaluated by this
                        // Evaluator - and this is not re-evaluation
                        submittedDoc = docDao.getDocumentByIdWithEvaluations(submittedDoc.getId());
                        Set<Evaluation> evaluations = submittedDoc.getEvaluations();
                        Evaluation eval = getOldEvaluationByThisEvaluator(evaluations, evaluatorId);

                        if (eval == null) {
                            // Create a Evaluation object and store the
                            // evaluation
                            eval = new Evaluation();
                            eval.setComments(comments);
                            eval.setEvaluatedBy(evaluatedBy);
                            eval.setOutOfTotal(maxGrades);
                            eval.setResult(gradesReceived);
                            eval.setEvaluationFor(submittedDoc);
                            evalDao.saveEvaluation(eval);
                        } else {
                            // Update the evaluation details and save the object
                            // again
                            eval.setComments(comments);
                            eval.setOutOfTotal(maxGrades);
                            eval.setResult(gradesReceived);
                            eval.setEvaluatedOnTimestamp(new Date());
                            evalDao.updateEvaluation(eval);
                        }

                        // TODO - calculate the final evaluations if
                        // disseminate_evaluations process is already done
                        // if (submittableMember.isFinalEvaluated()) {
                        // calculateAndSaveFinalEvaluations(submittableMember,
                        // submittedDoc, evaluatedBy);
                        // }

                        System.out.println("Successfully Submitted evaluation for document - "
                                + submittedDoc.getCmsDocumentPath() + " - by userId - " + submitterUserId);
                    } else {
                        System.out.println("This user hasnot yet submitted the submission document - ignoring..");
                    }
                } catch (final Exception e) {
                    System.out.println("Unable to persist evaluation for UserId - " + strSubmitterUserId);
                    e.printStackTrace();
                }
            }

            System.out.println("Successfully uploaded the evaluations for Member! - " + submittableMemberId);
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
     * 
     * @param submittableMember
     * @param doc
     * @param evaluatedBy
     */
    private void calculateAndSaveFinalEvaluations(Member submittableMember, Document doc, User evaluatedBy) {
        Evaluation finalEval = new Evaluation();
        finalEval.setEvaluatedBy(evaluatedBy);
        finalEval.setComments("Final Evaluations using all available evaluations from all assigned evaluators!");

        // TODO - Currently all final evaluation calculations are done
        // in percentage results from all individual document
        // evaluations
        finalEval.setOutOfTotal(100f);

        Set<Evaluation> evals = doc.getEvaluations();
        EvalType evalType = submittableMember.getFinalEvalType();

        // iterate over each individual evaluation to find out
        for (Evaluation eval : evals) {
            // based on type of EvalType
            switch (evalType)
            {
                case MAXIMUM: {
                    if (finalEval.getResult() == null) {
                        // Always save the percent result
                        finalEval.setResult(eval.getResult() / eval.getOutOfTotal());
                    } else {
                        if (finalEval.getResult() < (eval.getResult() / eval.getOutOfTotal())) {
                            finalEval.setResult(eval.getResult() / eval.getOutOfTotal());
                        }
                    }

                    break;
                }
                case MINIMUM: {
                    if (finalEval.getResult() == null) {
                        finalEval.setResult(eval.getResult() / eval.getOutOfTotal());
                    } else {
                        if (finalEval.getResult() > (eval.getResult() / eval.getOutOfTotal())) {
                            finalEval.setResult(eval.getResult() / eval.getOutOfTotal());
                        }
                    }

                    break;
                }
                case AVERAGE:
                default: {
                    if (finalEval.getResult() == null) {
                        finalEval.setResult(eval.getResult() / eval.getOutOfTotal());
                    } else {
                        // Just add all the evaluations, division by
                        // total number of evaluations will be done
                        // in the end
                        finalEval.setResult(finalEval.getResult() + (eval.getResult() / eval.getOutOfTotal()));
                    }
                    break;
                }
            }
        }

        // Calculate the final average
        if (evalType == EvalType.AVERAGE && finalEval.getResult() == null) {
            finalEval.setResult(finalEval.getResult() / evals.size());
        }

        // DAOs
        DocumentDao docDao = new DocumentDaoImpl();
        EvaluationDao evalDao = new EvaluationDaoImpl();

        evalDao.saveEvaluation(finalEval);
        doc.setFinalEvaluation(finalEval);
        docDao.updateDocument(doc);
    }

    /**
     * Check if there is any old evaluation by this Evaluator
     * 
     * @Note = there can be only one evaluation by one unique evaluator per
     *       document
     * @param evaluations
     * @param evaluatorId
     * @return previous Evaluation object
     */
    private Evaluation getOldEvaluationByThisEvaluator(Set<Evaluation> evaluations, Long evaluatorId) {
        // Go over each evaluation and find out the evaluation done by this
        // user-i.e. EVALUATOR
        for (Evaluation eval : evaluations) {
            if (eval.getEvaluatedBy().getId().equals(evaluatorId)) {
                return eval;
            }
        }
        return null;
    }

    /**
     * Check if given submitterUserId is supposed to be evaluated by me - i.e.
     * its in current user's submittersToEvaluate list of users
     */
    private boolean checkIfUserIsValidToEvaluate(Set<User> submittersToEvaluate, Long submitterUserId) {
        // Check if given email id user exists in given Set<User>
        for (User submitter : submittersToEvaluate) {
            if (submitter.getId().equals(submitterUserId)) {
                return true;
            }
        }
        return false;
    }
}
