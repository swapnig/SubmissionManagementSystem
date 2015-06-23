package edu.neu.ccis.sms.dao.submissions;

import java.util.List;

import edu.neu.ccis.sms.entity.submissions.Document;

/**
 * DAO interface for Document Entity bean; provides access methods for document information
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface DocumentDao {
    /**
     * Gets all the documents from all users submitted for all members
     * 
     * @return
     */
    public List<Document> getAllDocuments();

    /**
     * Gets a specific document by given document id
     * 
     * @param id
     * @return
     */
    public Document getDocument(Long id);

    /**
     * Update document details for a given document
     * 
     * @param modifiedDocument
     */
    public void updateDocument(Document modifiedDocument);

    /**
     * Delete a document
     * 
     * @param document
     */
    public void deleteDocument(final Document document);

    /**
     * Save a new document
     * 
     * @param newDocument
     */
    public void saveDocument(final Document newDocument);

    /**
     * Load document along with its all evaluations
     * 
     * @param documentId
     * @return
     */
    public Document getDocumentByIdWithEvaluations(final Long documentId);
}
