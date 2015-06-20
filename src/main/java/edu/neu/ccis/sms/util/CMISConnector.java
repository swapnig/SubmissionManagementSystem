package edu.neu.ccis.sms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.client.util.FileUtils;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;

/**
 * Utility class for CMIS opeations for CMS repository
 * 
 * @author Pramod R. Khare
 * @date 8-May-2015
 * @lastUpdate 10-June-2015
 */
public class CMISConnector {
    private static final Logger LOGGER = Logger.getLogger(CMISConnector.class.getName());

    // Connect to CMS repository and get the session
    private static Session session = null;

    public static Session getCMISSession(final CMISConfig configParams) {
        session = connectToRepository(configParams);
        return session;
    }

    /**
     * Connects to CMS Repository and creates the session for further work
     * 
     * @return
     */
    private static Session connectToRepository(final CMISConfig configParams) {
        SessionFactory sessionFactory = SessionFactoryImpl.newInstance();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(SessionParameter.USER, configParams.getCmsRepoUsername());
        parameters.put(SessionParameter.PASSWORD, configParams.getCmsRepoPswd());
        parameters.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
        parameters.put(SessionParameter.ATOMPUB_URL,
        // "http://localhost:8080/alfresco/cmisatom");
        // "http://localhost:8080/alfresco/api/-default-/public/cmis/versions/1.1/atom");
                configParams.getCmsRepoAtompubBindingUrl());

        List<Repository> repos = sessionFactory.getRepositories(parameters);
        LOGGER.info("Total Repositories in CMS = " + repos.size());

        Repository defaultRepo = repos.get(configParams.getCmsRepoNumber());
        LOGGER.info("Default Repo CMIS version supported = " + defaultRepo.getCmisVersionSupported());

        Session session = defaultRepo.createSession();
        LOGGER.info("Connected to the repository!!");
        return session;
    }

    /**
     * Create folder under given parent folder path, if it exists then don't do anything, otherwise create the new
     * subfolder under given parentFolderPath
     * 
     * @param parentFolderPath
     * @param folderName
     * @return
     * @throws CmisObjectNotFoundException
     */
    public static Folder createFolder(final String parentFolderPath, String folderName)
            throws CmisObjectNotFoundException
    {
        Folder parentFolder = null;
        Folder subFolder = null;
        try {
            parentFolder = (Folder) session.getObjectByPath(parentFolderPath);
            System.out.println("parentFolder found - " + parentFolder.getId());
        } catch (final CmisObjectNotFoundException onfe) {
            System.out.println("No such parentFolder found!!");
            throw onfe;
        }

        folderName = sanitizeFileFolderNames(folderName);
        // Then check if the new TOBE created folder already exists
        try {
            subFolder = (Folder) session.getObjectByPath(parentFolder.getPath() + "/" + folderName);
            System.out.println("Folder already existed!");
        } catch (final CmisObjectNotFoundException onfe) {
            Map<String, String> props = new HashMap<String, String>();
            props.put("cmis:objectTypeId", "cmis:folder");
            props.put("cmis:name", folderName);

            subFolder = parentFolder.createFolder(props);
            String subFolderId = subFolder.getId();

            System.out.println("Created new folder: " + subFolderId);
        }
        return subFolder;
    }

    /**
     * Create Folder under root folder directory
     * 
     * @param folderName
     * @return
     */
    public static Folder createFolderUnderRoot(String folderName) {

        Folder root = session.getRootFolder();
        Folder subFolder;
        // Then check if the new TOBE created folder already exists
        try {
            folderName = sanitizeFileFolderNames(folderName);
            subFolder = (Folder) session.getObjectByPath(root.getPath() + "/" + folderName);
            System.out.println("Folder already existed!");
        } catch (final CmisObjectNotFoundException onfe) {
            Map<String, String> props = new HashMap<String, String>();
            props.put("cmis:objectTypeId", "cmis:folder");
            props.put("cmis:name", folderName);

            subFolder = root.createFolder(props);
            String subFolderId = subFolder.getId();

            System.out.println("Created new folder: " + subFolderId);
        }
        return subFolder;
    }

    /**
     * Overloaded method - uploadToCMSUsingFileToFolderPath
     * 
     * @param parentFolderPath
     * @param file
     * @return
     * @throws IOException
     */
    public static Document uploadToCMSUsingFileToFolderPath(final String parentFolderPath, final File file)
            throws Exception
    {
        return uploadToCMSUsingFileToFolderPath(parentFolderPath, file.getName(),
                Files.probeContentType(file.toPath()), file);
    }

    /**
     * Overloaded method - uploadToCMSUsingFileToFolderPath
     * 
     * @param parentFolderPath
     * @param fileName
     * @param file
     * @return
     * @throws Exception
     */
    public static Document uploadToCMSUsingFileToFolderPath(final String parentFolderPath, final String fileName,
            final File file) throws Exception
    {
        return uploadToCMSUsingFileToFolderPath(parentFolderPath, fileName, Files.probeContentType(file.toPath()), file);
    }

    /**
     * Uploads the local document to CMS repo
     * 
     * @return - Document - newly uploaded document
     * @throws Exception
     */
    public static Document uploadToCMSUsingFileToFolderPath(final String parentFolderPath, String fileName,
            String fileType, final File file) throws Exception
    {

        Map<String, Object> props = null;
        Folder parentFolder = null;
        Document document = null;
        ContentStream contentStream = null;

        try {
            fileName = sanitizeFileFolderNames(fileName);
            parentFolder = (Folder) session.getObjectByPath(parentFolderPath);
            System.out.println("parentFolder found - " + parentFolder.getId());

            props = new HashMap<String, Object>();
            props.put("cmis:objectTypeId", "cmis:document");
            props.put("cmis:name", fileName);

            if (fileType == null) {
                fileType = "application/octet-stream";
            }
            contentStream = session.getObjectFactory().createContentStream(fileName, file.length(), fileType,
                    new FileInputStream(file));

            document = parentFolder.createDocument(props, contentStream, VersioningState.MAJOR);

            System.out.println("Created new document: " + document.getId() + " - Path - " + document.getPaths());
        } catch (final CmisContentAlreadyExistsException ccaee) {
            document = (Document) session.getObjectByPath(parentFolder.getPath() + "/" + fileName);
            System.out.println("Document already exists: " + fileName);
            throw ccaee;
        } catch (final FileNotFoundException e) {
            System.out.println("No such file found!");
            throw e;
        } catch (final CmisObjectNotFoundException onfe) {
            System.out.println("No such parentFolder found!!");
            throw onfe;
        }
        return document;
    }

    /**
     * Overloaded method - uploadToCMSUsingFileToFolder
     * 
     * @param parentFolder
     * @param file
     * @return
     * @throws IOException
     */
    public static Document uploadToCMSUsingFileToFolder(final Folder parentFolder, final File file) throws Exception {
        return uploadToCMSUsingFileToFolder(parentFolder, file.getName(), Files.probeContentType(file.toPath()), file);
    }

    /**
     * Overloaded method - uploadToCMSUsingFileToFolder
     * 
     * @param parentFolder
     * @param fileName
     * @param file
     * @return
     * @throws Exception
     */
    public static Document uploadToCMSUsingFileToFolder(final Folder parentFolder, final String fileName,
            final File file) throws Exception
    {
        return uploadToCMSUsingFileToFolder(parentFolder, fileName, Files.probeContentType(file.toPath()), file);
    }

    /**
     * 
     * @param parentFolder
     * @param fileName
     * @param fileType
     * @param file
     * @return
     * @throws Exception
     */
    public static Document uploadToCMSUsingFileToFolder(final Folder parentFolder, String fileName, String fileType,
            final File file) throws Exception
    {

        Map<String, Object> props = null;
        Document document = null;
        ContentStream contentStream = null;

        try {
            fileName = sanitizeFileFolderNames(fileName);
            props = new HashMap<String, Object>();
            props.put("cmis:objectTypeId", "cmis:document");
            props.put("cmis:name", fileName);

            if (fileType == null) {
                fileType = "application/octet-stream";
            }

            contentStream = session.getObjectFactory().createContentStream(fileName, file.length(), fileType,
                    new FileInputStream(file));

            document = parentFolder.createDocument(props, contentStream, VersioningState.MAJOR);

            System.out.println("Created new document: " + document.getId() + " - Path - " + document.getPaths());
        } catch (final CmisContentAlreadyExistsException ccaee) {
            document = (Document) session.getObjectByPath(parentFolder.getPath() + "/" + fileName);
            System.out.println("Document already exists: " + fileName);
            throw ccaee;
        } catch (final FileNotFoundException e) {
            System.out.println("No such file found!");
            throw e;
        } catch (final CmisObjectNotFoundException onfe) {
            System.out.println("No such parentFolder found!!");
            throw onfe;
        }
        return document;
    }

    /**
     * Check if given Object is directory or not
     * 
     * @param obj
     * @return boolean result if its a folder or document
     */
    public static boolean isFolder(CmisObject obj) {
        return ((String) obj.getPropertyValue("cmis:baseTypeId")).equalsIgnoreCase(BaseTypeId.CMIS_FOLDER.value());
    }

    /**
     * Get the document using CMS id
     * 
     * @param docId
     * @return Document
     */
    public static Document getDocumentById(final String docId) {
        return (Document) session.getObject(docId);
    }

    /**
     * Get the Folder using CMS id
     * 
     * @param folderId
     * @return Folder
     */
    public static Folder getFolderById(final String folderId) {
        return (Folder) session.getObject(folderId);
    }

    /**
     * Get the document using CMS document path
     * 
     * @param docPath
     * @return Document
     */
    public static Document getDocumentByPath(final String docPath) {
        Document parentFolder = null;
        try {
            parentFolder = (Document) session.getObjectByPath(docPath);
        } catch (final CmisObjectNotFoundException onfe) {
            parentFolder = null;
        }
        return parentFolder;
    }

    /**
     * Update the new version of document - first checkOut and then checkIn
     * 
     * @return new Document version
     */
    public static Document updateNewDocumentVersion(Document doc, String fileName, final File file) throws Exception {
        doc.refresh();
        ObjectId idOfCheckedOutDocument = doc.checkOut();
        Document workingCopy = (Document) session.getObject(idOfCheckedOutDocument);
        String fileType = Files.probeContentType(file.toPath());

        fileName = sanitizeFileFolderNames(fileName);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put("cmis:objectTypeId", "cmis:document");
        props.put("cmis:name", fileName);

        if (fileType == null) {
            fileType = "application/octet-stream";
        }

        ContentStream contentStream = session.getObjectFactory().createContentStream(fileName, file.length(), fileType,
                new FileInputStream(file));
        ObjectId objectId = workingCopy.checkIn(true, props, contentStream, "Resubmission!");
        doc = (Document) session.getObject(objectId);
        System.out.println("Version label is now:" + doc.getVersionLabel());

        return doc;
    }

    /**
     * Download the document to local disk from CMS repo
     * 
     * @param docId
     * @param destinationPath
     * @throws IOException
     */
    public static void downloadDocument(final String docId, final String destinationPath) throws IOException {
        FileUtils.download(docId, destinationPath, session);
    }

    /**
     * Rename the CMS folder
     * 
     * @param docId
     * @param destinationPath
     * @throws IOException
     */
    public static Folder renameFolder(final String folderId, String newFolderName) throws Exception {
        try {
            Folder folder = (Folder) session.getObject(folderId);
            newFolderName = sanitizeFileFolderNames(newFolderName);
            // Rename the folder with new name
            ObjectId obj = folder.rename(newFolderName, true);
            // Refresh the Folder details again from CMS
            folder = (Folder) session.getObject(obj);
            return folder;
        } catch (final CmisObjectNotFoundException onfe) {
            System.out.println("No such folder found!");
            throw onfe;
        }
    }

    /**
     * Delete folder and all its subfolders
     * @param folderId
     * @return boolean if the folder was completely deleted
     */
    public static boolean deleteFolder(final String folderId) {
        try {
            Folder folder = (Folder) session.getObject(folderId);
            // Delete folder and all its subfolders
            List<String> undeletedItems = folder.deleteTree(true, UnfileObject.DELETE, true);
            return (undeletedItems.size() > 0) ? false : true;
        } catch (final CmisObjectNotFoundException onfe) {
            System.out.println("No such folder found!");
            return false;
        }
    }

    /**
     * Delete a document mapped by given document id
     * @param docId
     * @return 
     */
    public static void deleteDocument(final String docId) {
        try {
            Document doc = getDocumentById(docId);
            doc.deleteAllVersions();
        } catch (final CmisObjectNotFoundException onfe) {
            System.out.println("No such document exists, no need to delete it!");
        }
    }

    /**
     * As there are certain characters which are not allowed in alfresco as file and folder names, we need to replace
     * those charasters with "_" chars < (less than), > (greater than), : (colon), " (double quote), / (forward slash),
     * \ (backslash), | (vertical bar or pipe), ? (question mark), * (asterisk)
     * 
     * @return sanitized string after replacing the invalid chars with all "_", and trimming its length to 254
     */
    public static String sanitizeFileFolderNames(String input) {
        LOGGER.info("Sanitizing Folder/File name = " + input);
        input = input.replaceAll("[/\\\\:*?\"<>|]", "_");
        input = input.substring(0, (input.length() > 254 ? 254 : input.length()));
        LOGGER.info("Sanitized Folder/File name = " + input);
        return input;
    }

    public static class CMISConfig {
        final String cmsRepoUsername;
        final String cmsRepoPswd;
        final String cmsRepoAtompubBindingUrl;
        final int cmsRepoNumber;

        public CMISConfig(final String cmsRepoUsername, final String cmsRepoPswd,
                final String cmsRepoAtompubBindingUrl, final int cmsRepoNumber)
        {
            this.cmsRepoAtompubBindingUrl = cmsRepoAtompubBindingUrl;
            this.cmsRepoNumber = cmsRepoNumber;
            this.cmsRepoPswd = cmsRepoPswd;
            this.cmsRepoUsername = cmsRepoUsername;
        }

        public String getCmsRepoUsername() {
            return cmsRepoUsername;
        }

        public String getCmsRepoPswd() {
            return cmsRepoPswd;
        }

        public String getCmsRepoAtompubBindingUrl() {
            return cmsRepoAtompubBindingUrl;
        }

        public int getCmsRepoNumber() {
            return cmsRepoNumber;
        }
    }
}
