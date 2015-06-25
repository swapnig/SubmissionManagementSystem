package edu.neu.ccis.sms.dao.submissions;

import java.util.List;

import edu.neu.ccis.sms.entity.submissions.Document;

/**
 * DAO interface for Document Entity bean; provides access methods for document entities
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface DocumentDao {
    /**
     * Gets all the documents from all users submitted for all members
     * 
     * @return - list of all documents submitted till now in this system
     */
    public List<Document> getAllDocuments();

    /**
     * Gets a specific document by given document id
     * 
     * @param id
     *            - document id
     * @return - Document obejct with given document id if it exists else null
     */
    public Document getDocument(Long id);

    /**
     * Update document details for a given document
     * 
     * @param modifiedDocument
     *            - modified document object
     */
    public void updateDocument(Document modifiedDocument);

    /**
     * Delete a document
     * 
     * @param document
     *            - document to be deleted
     */
    public void deleteDocument(final Document document);

    /**
     * Save a new document
     * 
     * @param newDocument
     *            - new document to be saved
     */
    public void saveDocument(final Document newDocument);

    /**
     * Load document along with its all evaluations
     * 
     * @param documentId
     *            - id of a document to be retrieved
     * @return - Document object with all its evaluations
     */
    public Document getDocumentByIdWithEvaluations(final Long documentId);
}
