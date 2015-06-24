package edu.neu.ccis.sms.servlets;

import java.io.IOException;
import java.util.Date;
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
    private static final Logger LOGGER = Logger.getLogger(UploadEvaluationsServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadEvaluationsServlet() {
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
     * Upon receiving file upload submission, parses the request to read upload data and saves the file on disk.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        LOGGER.info("Method - UploadEvaluationsServlet:doPost");

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
                        LOGGER.info("This submitterUserId " + submitterUserId
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
                        if (submittableMember.isFinalEvaluated()) {
                            calculateAndSaveFinalEvaluations(submittableMember, submittedDoc, evaluatedBy);
                        }

                        LOGGER.info("Successfully Submitted evaluation for document - "
                                + submittedDoc.getCmsDocumentPath() + " - by userId - " + submitterUserId);
                    } else {
                        LOGGER.info("This user hasnot yet submitted the submission document - ignoring..");
                    }
                } catch (final Exception e) {
                    LOGGER.info("Unable to persist evaluation for UserId - " + strSubmitterUserId);
                    e.printStackTrace();
                }
            }
            request.setAttribute(RequestKeys.PARAM_MESSAGE,
                    "Thank you! Evaluations received and saved successfully by system.");
            LOGGER.info("Successfully uploaded the evaluations for Member! - " + submittableMemberId);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute(RequestKeys.PARAM_MESSAGE,
                    "Failed to upload the evaluations. Please retry or contact administrator.");
            LOGGER.info("Failed to upload the evaluations : " + ex.getMessage());
        }
        request.getRequestDispatcher(JspViews.UPLOAD_EVALUATIONS_VIEW).forward(request, response);
    }

    /**
     * As evaluations for this submittable member are already out to students, these new evaluations should be directly
     * used for final Evaluation calculations. This function calculates the final evaluation for this document from its
     * new and all old evaluations, if there was already a final evaluation calculated then recalculates it considering
     * this current newly evaluation.
     * 
     * @param submittableMember
     * @param doc
     * @param evaluatedBy
     */
    private void calculateAndSaveFinalEvaluations(Member submittableMember, Document doc, User evaluatedBy) {

        // Get the old final evaluation - if there is any
        Evaluation finalEval = null;
        if (doc.getFinalEvaluation() != null) {
            finalEval = doc.getFinalEvaluation();
        } else {
            finalEval = new Evaluation();
        }

        finalEval.setEvaluatedBy(evaluatedBy);
        finalEval.setComments("Final Evaluations using all available evaluations from all assigned evaluators!");

        // TODO - Currently all final evaluation calculations are done
        // in percentage results from all individual document
        // evaluations
        finalEval.setOutOfTotal(100f);

        // Clear any previous results value
        finalEval.setResult(null);

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
                        finalEval.setResult(eval.getResult());
                    } else {
                        if (finalEval.getResult() < eval.getResult()) {
                            finalEval.setResult(eval.getResult());
                        }
                    }

                    break;
                }
                case MINIMUM: {
                    if (finalEval.getResult() == null) {
                        finalEval.setResult(eval.getResult());
                    } else {
                        if (finalEval.getResult() > eval.getResult()) {
                            finalEval.setResult(eval.getResult());
                        }
                    }

                    break;
                }
                case AVERAGE:
                default: {
                    if (finalEval.getResult() == null) {
                        finalEval.setResult(eval.getResult());
                    } else {
                        // Just add all the evaluations, division by
                        // total number of evaluations will be done
                        // in the end
                        finalEval.setResult(finalEval.getResult() + eval.getResult());
                    }
                    break;
                }
            }
        }

        // Calculate the final average
        if (evalType == EvalType.AVERAGE && finalEval.getResult() != null) {
            finalEval.setResult(finalEval.getResult() / evals.size());
        }

        // DAOs
        DocumentDao docDao = new DocumentDaoImpl();
        EvaluationDao evalDao = new EvaluationDaoImpl();

        // Save the final evaluation
        if (doc.getFinalEvaluation() == null) {
            evalDao.saveEvaluation(finalEval);
        } else {
            evalDao.updateEvaluation(finalEval);
        }

        // Update the document with this new evaluation
        doc.setFinalEvaluation(finalEval);
        docDao.updateDocument(doc);
    }

    /**
     * Check if there is any old evaluation by this Evaluator
     * 
     * @Note = there can be only one evaluation by one unique evaluator per document
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
     * Check if given submitterUserId is supposed to be evaluated by me - i.e. its in current user's
     * submittersToEvaluate list of users
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
