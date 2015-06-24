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
    private static final String CMIS_FOLDER_TYPE = "cmis:folder";
    private static final String CMIS_DOCUMENT_TYPE = "cmis:document";
    private static final String CMIS_OBJECT_TYPE = "cmis:objectTypeId";
    private static final String CMIS_BASE_TYPE = "cmis:baseTypeId";
    private static final String CMIS_NAME_PROPERTY = "cmis:name";
    private static final String BINARY_FILE_CONTENT_TYPE = "application/octet-stream";
    private static final String FILE_PATH_SEPARATOR = "/";

    /** CMS repository connection session */
    private static Session session = null;

    /**
     * Connect to CMS repository and get the session
     * 
     * @param configParams
     *            - config params for creating CMIS connection session, like CMS username, password, Atompub url,
     *            repository number
     * @return - CMS repository connection session
     */
    public static Session getCMISSession(final CMISConfig configParams) {
        session = connectToRepository(configParams);
        return session;
    }

    /**
     * Connects to CMS Repository and creates the session for further work
     * 
     * @param configParams
     *            - config params for creating CMIS connection session
     * @return session - returns a new session with CMS repository
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
        Repository defaultRepo = repos.get(configParams.getCmsRepoNumber());
        Session session = defaultRepo.createSession();
        LOGGER.info("Connected to the CMS repository!");
        return session;
    }

    /**
     * Create folder under given parent folder path, if it exists then don't do anything, otherwise create the new
     * subfolder under given parentFolderPath
     * 
     * @param parentFolderPath
     *            - file path of parent folder in CMS
     * @param folderName
     *            - name of new child folder to be created
     * @return - Folder instance of newly created folder if operation successful
     * @throws CmisObjectNotFoundException
     *             - if parent folder doesn't exist at given parent-folder-file-path, throws this exception
     */
    public static Folder createFolder(final String parentFolderPath, String folderName)
            throws CmisObjectNotFoundException
    {
        Folder parentFolder = null;
        Folder subFolder = null;
        try {
            parentFolder = (Folder) session.getObjectByPath(parentFolderPath);
        } catch (final CmisObjectNotFoundException onfe) {
            LOGGER.info("No such parentFolder found!");
            throw onfe;
        }

        folderName = sanitizeFileFolderNames(folderName);
        /** Now check if the new TO BE created folder already exists */
        try {
            subFolder = (Folder) session.getObjectByPath(parentFolder.getPath() + FILE_PATH_SEPARATOR + folderName);
        } catch (final CmisObjectNotFoundException onfe) {
            Map<String, String> props = new HashMap<String, String>();
            props.put(CMIS_OBJECT_TYPE, CMIS_FOLDER_TYPE);
            props.put(CMIS_NAME_PROPERTY, folderName);

            subFolder = parentFolder.createFolder(props);
            String subFolderId = subFolder.getId();
            LOGGER.info("Created new CMS folder: " + subFolderId);
        }
        return subFolder;
    }

    /**
     * Create Folder under root folder directory in CMS repository
     * 
     * @param folderName
     *            - name of new folder to be created
     * @return - Folder instance of newly created folder if operation successful
     */
    public static Folder createFolderUnderRoot(String folderName) {

        Folder root = session.getRootFolder();
        Folder subFolder;
        // Then check if the new TOBE created folder already exists
        try {
            folderName = sanitizeFileFolderNames(folderName);
            subFolder = (Folder) session.getObjectByPath(root.getPath() + FILE_PATH_SEPARATOR + folderName);
        } catch (final CmisObjectNotFoundException onfe) {
            Map<String, String> props = new HashMap<String, String>();
            props.put(CMIS_OBJECT_TYPE, CMIS_FOLDER_TYPE);
            props.put(CMIS_NAME_PROPERTY, folderName);

            subFolder = root.createFolder(props);
            String subFolderId = subFolder.getId();
            LOGGER.info("Created new CMS folder: " + subFolderId);
        }
        return subFolder;
    }

    /**
     * Uploads a file to a folder in CMS; The newly created document node in CMS will have same filename as the one
     * which is uploaded.
     * 
     * @param parentFolderPath
     *            - parent folder path in CMS
     * @param file
     *            - file to be uploaded
     * @return - returns a newly created document node in CMS repository
     * @throws IOException
     *             - Throws exception if it is unable to find the file-content type or fails to upload the file-contents
     *             to CMS repository
     */
    public static Document uploadToCMSUsingFileToFolderPath(final String parentFolderPath, final File file)
            throws Exception
    {
        return uploadToCMSUsingFileToFolderPath(parentFolderPath, file.getName(),
                Files.probeContentType(file.toPath()), file);
    }

    /**
     * Uploads a file to a folder in CMS; The newly created document node in CMS will have specific given filename and
     * not the name of file which is uploaded.
     * 
     * @param parentFolderPath
     *            - parent folder path in CMS
     * @param fileName
     *            - filename to be used in CMS document node creation
     * @param file
     *            - file to be uploaded
     * @return - returns a newly created document node in CMS repository
     * @throws Exception
     *             - Throws exception if it is unable to find the file-content type or fails to upload the file-contents
     *             to CMS repository
     */
    public static Document uploadToCMSUsingFileToFolderPath(final String parentFolderPath, final String fileName,
            final File file) throws Exception
    {
        return uploadToCMSUsingFileToFolderPath(parentFolderPath, fileName, Files.probeContentType(file.toPath()), file);
    }

    /**
     * Uploads a file to a folder in CMS; The newly created document node in CMS will have specific given filename and
     * not the name of file which is uploaded; and with specified fileType; This method can be useful when the
     * file-types are new or unknown in which case java.nio.file.Files call won't be able to probe the contenttype of a
     * given file
     * 
     * @param parentFolderPath
     *            - parent folder path in CMS
     * @param fileName
     *            - filename to be used in CMS document node creation
     * @param fileType
     *            - Content Type / MimeType of file being uploaded
     * @param file
     *            - file to be uploaded
     * @return - returns a newly created document node in CMS repository
     * @throws Exception
     *             - Throws exception if it fails to upload the file-contents to CMS repository
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
            props = new HashMap<String, Object>();
            props.put(CMIS_OBJECT_TYPE, CMIS_DOCUMENT_TYPE);
            props.put(CMIS_NAME_PROPERTY, fileName);

            if (fileType == null) {
                fileType = BINARY_FILE_CONTENT_TYPE;
            }
            contentStream = session.getObjectFactory().createContentStream(fileName, file.length(), fileType,
                    new FileInputStream(file));

            document = parentFolder.createDocument(props, contentStream, VersioningState.MAJOR);
        } catch (final CmisContentAlreadyExistsException ccaee) {
            document = (Document) session.getObjectByPath(parentFolder.getPath() + FILE_PATH_SEPARATOR + fileName);
            LOGGER.info("Document with same name already exists in CMS : " + fileName);
            throw ccaee;
        } catch (final FileNotFoundException e) {
            LOGGER.info("No such file found!");
            throw e;
        } catch (final CmisObjectNotFoundException onfe) {
            LOGGER.info("No such parentFolder found!");
            throw onfe;
        }
        return document;
    }

    /**
     * Uploads a file to a folder in CMS; The newly created document node in CMS will have same filename as the name of
     * file which is being uploaded.
     * 
     * @param parentFolder
     *            - parent folder node reference for CMS folder where new file will be uploaded
     * @param file
     *            - file to be uploaded
     * @return - returns a newly created document node in CMS repository
     * @throws Exception
     *             - Throws exception if it is unable to find the file-content type or fails to upload the file-contents
     *             to CMS repository
     */
    public static Document uploadToCMSUsingFileToFolder(final Folder parentFolder, final File file) throws Exception {
        return uploadToCMSUsingFileToFolder(parentFolder, file.getName(), Files.probeContentType(file.toPath()), file);
    }

    /**
     * Uploads a file to a folder in CMS; The newly created document node in CMS will have specific given filename and
     * not the name of file which is uploaded.
     * 
     * @param parentFolder
     *            - parent folder node reference for CMS folder where new file will be uploaded
     * @param fileName
     *            - filename to be used in CMS document node creation
     * @param file
     *            - file to be uploaded
     * @return - returns a newly created document node in CMS repository
     * @throws Exception
     *             - Throws exception if it is unable to find the file-content type or fails to upload the file-contents
     *             to CMS repository
     */
    public static Document uploadToCMSUsingFileToFolder(final Folder parentFolder, final String fileName,
            final File file) throws Exception
    {
        return uploadToCMSUsingFileToFolder(parentFolder, fileName, Files.probeContentType(file.toPath()), file);
    }

    /**
     * Uploads a file to a folder in CMS; The newly created document node in CMS will have specific given filename and
     * not the name of file which is uploaded; and with specified fileType; This method can be useful when the
     * file-types are new or unknown in which case java.nio.file.Files call won't be able to probe the contenttype of a
     * given file
     * 
     * @param parentFolder
     *            - parent folder node reference for CMS folder where new file will be uploaded
     * @param fileName
     *            - filename to be used in CMS document node creation
     * @param fileType
     *            - Content Type / MimeType of file being uploaded
     * @param file
     *            - file to be uploaded
     * @return - returns a newly created document node in CMS repository
     * @throws Exception
     *             - Throws exception if it fails to upload the file-contents to CMS repository
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
            props.put(CMIS_OBJECT_TYPE, CMIS_DOCUMENT_TYPE);
            props.put(CMIS_NAME_PROPERTY, fileName);

            if (fileType == null) {
                fileType = BINARY_FILE_CONTENT_TYPE;
            }

            contentStream = session.getObjectFactory().createContentStream(fileName, file.length(), fileType,
                    new FileInputStream(file));

            document = parentFolder.createDocument(props, contentStream, VersioningState.MAJOR);
        } catch (final CmisContentAlreadyExistsException ccaee) {
            document = (Document) session.getObjectByPath(parentFolder.getPath() + FILE_PATH_SEPARATOR + fileName);
            LOGGER.info("Document with same name already exists in CMS : " + fileName);
            throw ccaee;
        } catch (final FileNotFoundException e) {
            LOGGER.info("No such file found!");
            throw e;
        } catch (final CmisObjectNotFoundException onfe) {
            LOGGER.info("No such parentFolder found!!");
            throw onfe;
        }
        return document;
    }

    /**
     * Check if given CMIS Object is directory or not; This method checks the "cmis:baseTypeId" - basic content type
     * property value of this CMIS object
     * 
     * @param CmisObject
     *            obj
     * @return boolean result if its a folder or document
     */
    public static boolean isFolder(CmisObject obj) {
        return ((String) obj.getPropertyValue(CMIS_BASE_TYPE)).equalsIgnoreCase(BaseTypeId.CMIS_FOLDER.value());
    }

    /**
     * Get the document using CMS document node id
     * 
     * @param docId
     *            - id of document node in CMS
     * @return Document - if it exists returns Document CMIS object reference else null
     */
    public static Document getDocumentById(final String docId) {
        return (Document) session.getObject(docId);
    }

    /**
     * Get the Folder using CMS folder node id
     * 
     * @param folderId
     *            - id of folder node in CMS
     * @return Folder - if it exists returns Folder CMIS object reference else null
     */
    public static Folder getFolderById(final String folderId) {
        return (Folder) session.getObject(folderId);
    }

    /**
     * Get the document using CMS document path
     * 
     * @param docPath
     *            - path of document
     * @return Document - if it exists at given path then returns Document CMIS object reference else null
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
     * Update an existing document with the new version of a document; Here we first checkOut old document and then
     * checkIn and upload the new modified document contents into CMS repository
     * 
     * @param doc
     *            - CMIS Document object reference - which is getting updated
     * @param fileName
     *            - filename of newly updated version of document
     * @param file
     *            - actual file contents which will be checked-in as newer version of document
     * @return - new version of document
     * @throws Exception
     *             - throws exception if it fails to checkIn new file contents of document
     */
    public static Document updateNewDocumentVersion(Document doc, String fileName, final File file) throws Exception {
        doc.refresh();
        ObjectId idOfCheckedOutDocument = doc.checkOut();
        Document workingCopy = (Document) session.getObject(idOfCheckedOutDocument);
        String fileType = Files.probeContentType(file.toPath());

        fileName = sanitizeFileFolderNames(fileName);
        Map<String, Object> props = new HashMap<String, Object>();
        props.put(CMIS_OBJECT_TYPE, CMIS_DOCUMENT_TYPE);
        props.put(CMIS_NAME_PROPERTY, fileName);

        if (fileType == null) {
            fileType = BINARY_FILE_CONTENT_TYPE;
        }

        ContentStream contentStream = session.getObjectFactory().createContentStream(fileName, file.length(), fileType,
                new FileInputStream(file));
        ObjectId objectId = workingCopy.checkIn(true, props, contentStream, "Resubmission!");
        doc = (Document) session.getObject(objectId);
        LOGGER.info("Version label for document: " + doc.getName() + " is now:" + doc.getVersionLabel());

        return doc;
    }

    /**
     * Download the document to given filepath from CMS repository
     * 
     * @param docId
     *            - CMS document id
     * @param destinationPath
     *            - destination file path where the document will be downloaded
     * @throws IOException
     *             - throws exception if no such destination path exists or if it fails to download document contents
     *             form CMS repository
     */
    public static void downloadDocument(final String docId, final String destinationPath) throws IOException {
        FileUtils.download(docId, destinationPath, session);
    }

    /**
     * Rename the folder in CMS repository
     * 
     * @param folderId
     *            - id of folder CMS node
     * @param newFolderName
     *            - new name for folder
     * @return - newly renamed folder CMIS object reference
     * @throws Exception
     *             - throws exception if it fails to rename the folder CMS node, maybe due to persmissions or invalid
     *             foldername characters
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
            LOGGER.info("No such folder found!");
            throw onfe;
        }
    }

    /**
     * Delete folder and all its subfolders
     * 
     * @param folderId
     *            - CMS id of folder to be deleted
     * @return boolean - true if the folder is completely deleted, false if it fails
     */
    public static boolean deleteFolder(final String folderId) {
        try {
            Folder folder = (Folder) session.getObject(folderId);
            // Delete folder and all its subfolders
            List<String> undeletedItems = folder.deleteTree(true, UnfileObject.DELETE, true);
            return (undeletedItems.size() > 0) ? false : true;
        } catch (final CmisObjectNotFoundException onfe) {
            LOGGER.info("No such folder found!");
            return false;
        }
    }

    /**
     * Delete a document mapped by given document id
     * 
     * @param docId
     *            - id of document to be deleted
     */
    public static void deleteDocument(final String docId) {
        try {
            Document doc = getDocumentById(docId);
            doc.deleteAllVersions();
        } catch (final CmisObjectNotFoundException onfe) {
            LOGGER.info("No such document exists, no need to delete it!");
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
        input = input.replaceAll("[/\\\\:*?\"<>|]", "_");
        input = input.substring(0, (input.length() > 254 ? 254 : input.length()));
        return input;
    }

    /**
     * Config class for initializing CMIS API connection with CMS
     * 
     * @author Pramod R Khare
     */
    public static class CMISConfig {
        /** CMS repo connection - username */
        final String cmsRepoUsername;

        /** CMS repo connection - password */
        final String cmsRepoPswd;

        /** CMS repo connection - Atompub Binding Url */
        final String cmsRepoAtompubBindingUrl;

        /**
         * CMS repo connection - repository number - as there can be mulitple repositories installed in a single CMS
         * server
         */
        final int cmsRepoNumber;

        /** Default constructor - makes sure repo number is non negative */
        public CMISConfig(final String cmsRepoUsername, final String cmsRepoPswd,
                final String cmsRepoAtompubBindingUrl, final int cmsRepoNumber)
        {
            this.cmsRepoAtompubBindingUrl = cmsRepoAtompubBindingUrl;
            this.cmsRepoNumber = (cmsRepoNumber < 0) ? 0 : cmsRepoNumber;
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
