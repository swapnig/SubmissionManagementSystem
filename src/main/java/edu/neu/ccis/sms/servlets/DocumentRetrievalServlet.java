package edu.neu.ccis.sms.servlets;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
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

import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.submissions.Document;
import edu.neu.ccis.sms.entity.users.User;
import edu.neu.ccis.sms.util.CMISConnector;

/**
 * Servlet implementation class DocumentRetrievalServlet
 * 
 * @author Pramod R. Khare
 * @date 12-June-2015
 * @lastUpdate 13-June-2015
 */
@WebServlet(name = "DocumentRetrievalServlet", urlPatterns = { "/DocumentRetrievalForEvaluation" })
@MultipartConfig
public class DocumentRetrievalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public DocumentRetrievalServlet() {
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

            // Get parameter "memberId" for which submissions to be downloaded
            Long memberId = Long.parseLong(request.getParameter("memberId"));

            UserDao userDao = new UserDaoImpl();
            User reviewer = userDao.getUserByIdWithSubmittersToEvaluateMappings(userId);
            Set<User> evaluateSubmitters = reviewer.getSubmittersToEvaluateForMemberId(memberId);

            File tempDir = new File(System.getProperty("java.io.tmpdir"));
            File solutionsDir = new File(tempDir.getAbsolutePath() + File.separator + "solutions");

            // Clean any previous solution download directories, if any
            if (solutionsDir.exists()) {
                System.out.println("Deleting the previous Solutions tmp directory!");
                FileUtils.deleteDirectory(solutionsDir);
            }

            solutionsDir.mkdirs();

            for (User user : evaluateSubmitters) {
                System.out.println("Downloading document user - " + user.getEmail());
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
                    System.out.println("Unable to download document - " + doc.getFilename());
                    e.printStackTrace();
                }
            }

            System.out.println("Document submissions downloaded successfully from CMS!!");

            // Checks to see if the directory contains some files.
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
                System.out.println("zip downloaded successfully!!");
            }

            // TODO Cleanup the files which were downloaded
            FileUtils.deleteDirectory(solutionsDir);

            System.out.println("Submissions downloaded successfully for evaluation!!");
        } catch (Exception ex) {
            request.setAttribute("message", "There was an error: " + ex.getMessage());
            // redirects client to message page
            System.out.println(ex.getMessage());
            response.sendRedirect("pages/error.jsp");
        }
    }

    /**
     * Compress the given directory with all its files.
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
     * 
     * @param dirObj
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
            System.out.println(" Adding: " + files[i].getAbsolutePath());
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
