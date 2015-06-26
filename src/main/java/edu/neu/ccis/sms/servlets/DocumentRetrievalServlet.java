package edu.neu.ccis.sms.servlets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;

import edu.neu.ccis.sms.constants.JspViews;
import edu.neu.ccis.sms.constants.RequestKeys;
import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.submissions.Document;
import edu.neu.ccis.sms.entity.users.User;
import edu.neu.ccis.sms.util.CMISConnector;

/**
 * Servlet implementation class DocumentRetrievalServlet; Download the submission documents for given submittable member
 * as ZIP file.
 * 
 * Servlet takes one request parameter - 1) memberId - submittable member id for which current user has EVALUATOR role
 * and is trying to download the submissions for evaluating them. First we check who all submitters are mapped for given
 * member to this evaluator. For each submitter user - we check if that user has submitted a document or not, if yes
 * then download the document contents from CMS into local temporary directory before compressing it and sending as ZIP
 * stream to EVALUATOR.
 * 
 * @author Pramod R. Khare
 * @date 12-June-2015
 * @lastUpdate 13-June-2015
 */
@WebServlet(name = "DocumentRetrievalServlet", urlPatterns = { "/DocumentRetrievalForEvaluation" })
@MultipartConfig
public class DocumentRetrievalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DocumentRetrievalServlet.class.getName());

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentRetrievalServlet() {
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
     * Download the submission documents for given submittable member as ZIP file.
     * 
     * Servlet takes one request parameter - 1) memberId - submittable member id for which current user has EVALUATOR
     * role and is trying to download the submissions for evaluating them. First we check who all submitters are mapped
     * for given member to this evaluator. For each submitter user - we check if that user has submitted a document or
     * not, if yes then download the document contents from CMS into local temporary directory before compressing it and
     * sending as ZIP stream to EVALUATOR.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        LOGGER.info("Method - DocumentRetrievalServlet:doPost");
        try {
            HttpSession session = request.getSession(false);
            Long userId = (Long) session.getAttribute(SessionKeys.keyUserId);

            // Get parameter "memberId" for which submissions to be downloaded
            Long memberId = Long.parseLong(request.getParameter("memberId"));
            UserDao userDao = new UserDaoImpl();
            User reviewer = userDao.getUserByIdWithSubmittersToEvaluateMappings(userId);

            // Get all the submitters which are to be evalauted by current logged-in user - EVALUATOR
            Set<User> evaluateSubmitters = reviewer.getSubmittersToEvaluateForMemberId(memberId);

            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File solutionsDir = new File(tempDir.getAbsolutePath() + File.separator + "solutions");

            // Clean any previous solution download directories, if any
            if (solutionsDir.exists()) {
                LOGGER.info("Deleting the previous Solutions tmp directory!");
                FileUtils.deleteDirectory(solutionsDir);
            }

            // Creating a temporary directory to download the individual solutions from submitters from CMS
            solutionsDir.mkdirs();

            // For each submitter, check if he/she has submitted any document into the SMS system, if yes download it
            // from CMS, else skip to next submitter
            for (User user : evaluateSubmitters) {
                Document doc = null;
                try {
                    doc = userDao.getSubmissionDocumentForMemberIdByUserId(user.getId(), memberId);

                    // Create a necessary folder structure and then download the
                    // submission document there
                    File dir = new File(solutionsDir.getAbsolutePath() + File.separator + user.getEmail());
                    dir.mkdirs();

                    // Create file inside this directory with submission file
                    // name
                    File file = new File(dir.getAbsolutePath() + File.separator + doc.getFilename());
                    file.createNewFile();

                    CMISConnector.downloadDocument(doc.getCmsDocId(), file.getAbsolutePath());
                } catch (final Exception e) {
                    LOGGER.info("Unable to download document - " + doc.getFilename());
                    e.printStackTrace();
                }
            }

            // Checks to see if the temporary solutions directory contains some files.
            if (solutionsDir != null && solutionsDir.list().length > 0) {
                // Call the zipFiles method for creating a zip stream.
                byte[] zip = zipFiles(solutionsDir);

                // Sends the response back to the user / browser. The content
                // for zip file type is "application/zip". We also set the
                // content disposition as attachment for the browser to show a
                // dialog that will let user choose what action will he do to
                // the sent content.
                ServletOutputStream sos = response.getOutputStream();
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename=\"solutions.zip\"");

                sos.write(zip);
                sos.flush();
                LOGGER.info("zip downloaded successfully!!");
            } else {
                // Send the error message to UI page to show - that there are no submissions to evaluate or Conductor is
                // yet to disseminate the submissions for evaluations
                LOGGER.info("There are no submission available for evaluation."
                        + "<br/>Either Conductor has not yet allocated the evaluators"
                        + " OR there are no submissions allocated to you.");
                request.setAttribute(RequestKeys.PARAM_MESSAGE, "There are no submission available for evaluation."
                        + "<br/>Either Conductor has not yet allocated the evaluators"
                        + " OR there are no submissions allocated to you.");
                request.getRequestDispatcher(JspViews.DOWNLOAD_SUBMISSIONS_VIEW).forward(request, response);
            }

            // TODO Cleanup the files which were downloaded
            FileUtils.deleteDirectory(solutionsDir);
            LOGGER.info("Submissions downloaded successfully for evaluation!!");
        } catch (final Exception ex) {
            ex.printStackTrace();
            request.setAttribute(RequestKeys.PARAM_MESSAGE,
                    "Failed to download submissions zip file for evaluation. Please try again or contact administrator.");
            LOGGER.info("Failed to download submissions zip file for evaluation : " + ex.getMessage());
            request.getRequestDispatcher(JspViews.DOWNLOAD_SUBMISSIONS_VIEW).forward(request, response);
        }
    }

    /**
     * Compress the given directory with all its files into a Zip output stream
     * 
     * @param directory
     *            - Directory to be compressed into ZIP file
     * @return - ZIP output stream bytes
     * @throws IOException
     */
    private byte[] zipFiles(File directory) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);

        addDir(directory, zos, "solutions" + File.separator);

        zos.flush();
        baos.flush();
        zos.close();
        baos.close();

        return baos.toByteArray();
    }

    /**
     * Add directory entry into zipoutputstream;
     * 
     * @param dirObj
     *            - directory to be added into zip output stream
     * @throws IOException
     */
    private void addDir(File dirObj, ZipOutputStream out, String zipEntryRelativePath) throws IOException {
        File[] files = dirObj.listFiles();
        byte[] tmpBuf = new byte[2048];

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                addDir(files[i], out, (zipEntryRelativePath + files[i].getName() + File.separator));
                continue;
            }
            FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
            LOGGER.info(" Adding: " + files[i].getAbsolutePath());
            out.putNextEntry(new ZipEntry(zipEntryRelativePath + files[i].getName()));
            int len;
            while ((len = in.read(tmpBuf)) > 0) {
                out.write(tmpBuf, 0, len);
            }
            out.closeEntry();
            in.close();
        }
    }
}
