package edu.neu.ccis.sms.servlets;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.enums.Action;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import edu.neu.ccis.sms.constants.SessionKeys;
import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.dao.submissions.DocumentDao;
import edu.neu.ccis.sms.dao.submissions.DocumentDaoImpl;
import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.submissions.Document;
import edu.neu.ccis.sms.entity.users.User;
import edu.neu.ccis.sms.util.CMISConnector;

/**
 * Servlet implementation class UploadForMember
 * 
 * @author Pramod R. Khare
 * @date 2-June-2015
 * @lastUpdate 8-June-2015
 */
@WebServlet(name = "UploadForMember", urlPatterns = { "/UploadForMember" })
@MultipartConfig
public class UploadForMember extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadForMember() {
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

    // upload settings
    // sets memory threshold - beyond which files are stored in disk
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 5; // 5MB
    // sets maximum size of upload file
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 50; // 50MB
    // sets maximum size of request (include file + form data)
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 60; // 60MB

    /**
     * Upon receiving file upload submission, parses the request to read upload
     * data and saves the file on disk.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        // checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(request)) {
            // if not, we stop here
            System.out.println("Error: Form must have enctype=multipart/form-data.");
            getServletContext().getRequestDispatcher("/pages/error.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        Long userId = (Long) session.getAttribute(SessionKeys.keyUserId);

        String submittedFromRemoteAddress = request.getRemoteAddr();

        // configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);

        // sets temporary location to store files
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        try {
            // parses the request's content to extract file data
            List<FileItem> formItems = upload.parseRequest(request);

            String fileName = null;
            File storeFile = null;
            Long memberIdToUploadFor = null;

            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                        System.out.println("Non-Form-Field - item.getName() - " + item.getName());

                        fileName = FilenameUtils.getName(item.getName());
                        String fileNameSuffix = FilenameUtils.getExtension(fileName);
                        storeFile = File.createTempFile(fileName, "." + fileNameSuffix);

                        // saves the file on disk
                        item.write(storeFile);
                    } else {
                        if (item.getFieldName().equals("memberId")) {
                            memberIdToUploadFor = Long.parseLong(item.getString());
                        }
                    }
                }
            }

            // Upload this file into CMS only if we have a valid memberId,
            // uploaded file, filename from request
            if (null != memberIdToUploadFor && null != storeFile && null != fileName) {
                System.out.println("Saving submission to SMS and CMS both!...");
                UserDao userDao = new UserDaoImpl();
                User submitter = userDao.getUser(userId);

                MemberDao memberDao = new MemberDaoImpl();
                Member member = memberDao.getMember(memberIdToUploadFor);

                String memberFolderPath = member.getCmsFolderPath();

                System.out.println("Checking user-email folder under member");
                // Create the email-id folder for given user if it doesn't exist
                Folder submitterCMSFolder = CMISConnector.createFolder(memberFolderPath, submitter.getEmail());

                DocumentDao docDao = new DocumentDaoImpl();
                org.apache.chemistry.opencmis.client.api.Document doc = null;

                // First check if user has already submitted for this
                // member already - meaning is it a resubmission, then get the
                // document reference
                // Get the submission document reference for given memberId 
                // Document smsDoc = submitter.getSubmissionDocumentForMemberId(memberIdToUploadFor);

                Document smsDoc = userDao.getSubmissionDocumentForMemberIdByUserId(userId, memberIdToUploadFor);

                // If there is no previous submission by this user for this
                // memberId
                if (null == smsDoc) {
                    // Upload the uploaded-file into CMS
                    doc = CMISConnector.uploadToCMSUsingFileToFolder(submitterCMSFolder, fileName, storeFile);

                    // Save the Document object into SMS database
                    smsDoc = new Document();
                    smsDoc.setFilename(fileName);
                    smsDoc.setCmsDocContentUrl(doc.getContentUrl());
                    smsDoc.setCmsDocId(doc.getId());
                    smsDoc.setCmsDocumentPath(doc.getPaths().get(0));
                    smsDoc.setCmsDocVersion(doc.getVersionLabel());
                    smsDoc.setContentType((String) doc.getPropertyValue("cmis:contentStreamMimeType"));
                    smsDoc.addSubmittedBy(submitter);
                    smsDoc.setSubmittedForMember(member);
                    smsDoc.setSubmittedFromRemoteAddress(submittedFromRemoteAddress);

                    docDao.saveDocument(smsDoc);
                } else {
                    // Resubmission scenario - get previous Document reference
                    // and update it with newer version
                    doc = CMISConnector.getDocumentByPath(smsDoc.getCmsDocumentPath());

                    // Check if we have CHECK_OUT permission
                    if (null != doc && doc.getAllowableActions().getAllowableActions().contains(Action.CAN_CHECK_OUT)) {
                        doc = CMISConnector.updateNewDocumentVersion(doc, fileName, storeFile);

                        // Update the Document
                        smsDoc.setFilename(fileName);
                        smsDoc.setCmsDocContentUrl(doc.getContentUrl());
                        smsDoc.setCmsDocId(doc.getId());
                        smsDoc.setCmsDocumentPath(doc.getPaths().get(0));
                        smsDoc.setCmsDocVersion(doc.getVersionLabel());
                        smsDoc.setContentType((String) doc.getPropertyValue("cmis:contentStreamMimeType"));
                        smsDoc.setSubmittedFromRemoteAddress(submittedFromRemoteAddress);

                        docDao.updateDocument(smsDoc);
                    } else {
                        throw new Exception("Unable to update document version");
                    }
                }
                System.out.println("Successfully uploaded the document for Member! - " + doc.getPaths().get(0));
            }
            // redirects client to message page
            response.sendRedirect("pages/submit_to_member.jsp");
        } catch (Exception ex) {
            request.setAttribute("message", "There was an error: " + ex.getMessage());
            // redirects client to message page
            System.out.println(ex.getMessage());
            response.sendRedirect("pages/error.jsp");
        }
    }
}
