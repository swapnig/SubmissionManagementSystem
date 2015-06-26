package edu.neu.ccis.sms.servlets;

import java.io.IOException;
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
 * Servlet implementation class DisseminateEvaluationsServlet; This servlet serves requests to disseminate evaluations
 * for a submittable member to their submitters; As each submission from submitters can have multiple evaluators
 * alloted, we first need to calculate the final evaluation for each submission from all the evaluations received from
 * those evaluators. So for each submission we take all its available evaluations and a strategy type for
 * final-evaluation calculations like Averaging all evaluation results to calculate the final.
 * 
 * This servlet takes two request parameters -
 * 
 * 1) submittable member's id - submittableMemberId - The submittable member for which all evaluations
 * 
 * 2) evalType - This value determines how the final evaluation for a submission will be calculated e.g. AVERAGE,
 * MINIMUM, MAXIMUM of all evaluation results
 * 
 * Note - In current implementation, Final evaluations are always out of maximum 100. i.e. percentage results
 * 
 * @author Pramod R. Khare
 * @date 2-June-2015
 * @lastUpdate 8-June-2015
 */
@WebServlet(name = "DisseminateEvaluationsServlet", urlPatterns = { "/DisseminateEvaluations" })
@MultipartConfig
public class DisseminateEvaluationsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DisseminateEvaluationsServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DisseminateEvaluationsServlet() {
        super();
    }

    /**
     * Forwards to doPost(request, response) method, See javadoc comments of
     * {@link #doPost(HttpServletRequest, HttpServletResponse)}
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * Calculate the final Evaluations for all the submitted documents for given submittable memberId; As each
     * submission from submitters can have multiple evaluators alloted, we first need to calculate the final evaluation
     * for each submission from all the evaluations received from those evaluators. So for each submission we take all
     * its available evaluations and a strategy type for final-evaluation calculations like Averaging all evaluation
     * results to calculate the final.
     * 
     * This servlet takes two request parameters -
     * 
     * 1) submittable member's id - submittableMemberId - The submittable member for which all evaluations
     * 
     * 2) evalType - This value determines how the final evaluation for a submission will be calculated e.g. AVERAGE,
     * MINIMUM, MAXIMUM of all evaluation results
     * 
     * Note - In current implementation, Final evaluations are always out of maximum 100. i.e. percentage results
     * 
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        LOGGER.info("Method - DisseminateEvaluationsServlet:doPost");
        try {
            HttpSession session = request.getSession(false);
            Long conductorUserId = (Long) session.getAttribute(SessionKeys.keyUserId);

            // DAOs
            UserDao userDao = new UserDaoImpl();
            MemberDao memberDao = new MemberDaoImpl();
            DocumentDao docDao = new DocumentDaoImpl();
            EvaluationDao evalDao = new EvaluationDaoImpl();

            User conductorUser = userDao.getUser(conductorUserId);
            Long submittableMemberId = Long.parseLong(request.getParameter("submittableMemberId"));

            // If invalid EvalType is sent from UI then exception will happen -
            // so no evaluations occur until proper EvalType is available
            EvalType evalType = EvalType.valueOf(request.getParameter("evalType"));
            Member submittableMember = memberDao.getMemberByIdWithSubmissions(submittableMemberId);

            // Get all submissions for this submittable member
            Set<Document> submissions = submittableMember.getSubmissions();

            // First update the Member with evaluation type and isFinalEvaluated
            submittableMember.setFinalEvaluated(true);
            submittableMember.setFinalEvalType(evalType);
            // Update the Member info
            memberDao.updateMember(submittableMember);

            // Get every submission document and get all its evaluations
            for (Document submission : submissions) {
                // Get all the evaluations first for this document
                Document doc = docDao.getDocumentByIdWithEvaluations(submission.getId());

                // Get the old final evaluation - if there is any, else create new final evaluation and calculate the
                // results for it
                Evaluation finalEval = calculateFinalEvaluation(doc, conductorUser, evalType);

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
            request.setAttribute(RequestKeys.PARAM_MESSAGE,
                    "Disseminated evaluations successfully by calculating final evaluations for all submissions.");
            LOGGER.info("Successfully calculated final evaluations for Member! - " + submittableMemberId);
        } catch (Exception ex) {
            ex.printStackTrace();
            request.setAttribute(RequestKeys.PARAM_MESSAGE,
                    "Failed to disseminate evaluations. Please retry or contact administrator.");
            // redirects client to message page
            LOGGER.info("Failed to disseminate evaluations : " + ex.getMessage());
        }
        request.getRequestDispatcher(JspViews.DISSEMINATE_EVALUATIONS_VIEW).forward(request, response);
    }

    /**
     * Calculate The final evaluation for given document with given EvalType
     * 
     * @param doc
     *            - submission document for which we are calculating the final evaluations
     * @param conductorUser
     *            - user who is disseminating the evaluations i.e. Conductor user
     * @param evalType
     *            - EvalType which decides how the final evaluation will be calculated from all available evaluations
     * @return - final Evaluation object for current submission document
     */
    private Evaluation calculateFinalEvaluation(final Document doc, final User conductorUser, final EvalType evalType) {

        // Create or get old final evaluation object reference
        Evaluation finalEval = null;
        if (doc.getFinalEvaluation() != null) {
            finalEval = doc.getFinalEvaluation();
        } else {
            finalEval = new Evaluation();
        }

        finalEval.setEvaluatedBy(conductorUser);
        finalEval.setComments("Final Evaluations using all available evaluations from all assigned evaluators");

        // TODO - Currently all final evaluation calculations are done
        // in percentage results from all individual document
        // evaluations
        finalEval.setOutOfTotal(100f);

        // Clear any previous results value
        finalEval.setResult(null);

        Set<Evaluation> evals = doc.getEvaluations();

        // iterate over each individual evaluation to find out
        for (Evaluation eval : evals) {
            // based on type of EvalType
            switch (evalType)
            {
                case MAXIMUM: {
                    if (finalEval.getResult() == null) {
                        // Always save the percent result
                        finalEval.setResult((eval.getResult() / eval.getOutOfTotal()) * 100);
                    } else {
                        float percentEvalResult = (eval.getResult() / eval.getOutOfTotal()) * 100;
                        if (finalEval.getResult() < percentEvalResult) {
                            finalEval.setResult(percentEvalResult);
                        }
                    }

                    break;
                }
                case MINIMUM: {
                    if (finalEval.getResult() == null) {
                        finalEval.setResult((eval.getResult() / eval.getOutOfTotal()) * 100);
                    } else {
                        float percentEvalResult = (eval.getResult() / eval.getOutOfTotal()) * 100;
                        if (finalEval.getResult() > percentEvalResult) {
                            finalEval.setResult(percentEvalResult);
                        }
                    }

                    break;
                }
                case AVERAGE:
                default: {
                    if (finalEval.getResult() == null) {
                        finalEval.setResult((eval.getResult() / eval.getOutOfTotal()) * 100);
                    } else {
                        // Just add all the evaluations, division by
                        // total number of evaluations will be done
                        // in the end
                        float percentEvalResult = (eval.getResult() / eval.getOutOfTotal()) * 100;
                        finalEval.setResult(finalEval.getResult() + percentEvalResult);
                    }
                    break;
                }
            }
        }

        // Calculate the final average when EvalType is AVERAGE
        if (evalType == EvalType.AVERAGE && finalEval.getResult() != null) {
            finalEval.setResult(finalEval.getResult() / evals.size());
        }
        // Return the final calculated evaluation
        return finalEval;
    }
}
