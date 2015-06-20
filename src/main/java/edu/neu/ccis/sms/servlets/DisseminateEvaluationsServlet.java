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
 * Servlet implementation class DisseminateEvaluationsServlet
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
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    /**
     * Calculate the final Evaluations for all the submitted documents for given memberId
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

                // Get the old final evaluation - if there is any
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

                Set<Evaluation> evals = doc.getEvaluations();

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
                if (evalType == EvalType.AVERAGE && finalEval.getResult() == null) {
                    finalEval.setResult(finalEval.getResult() / evals.size());
                }

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
}
