package edu.neu.ccis.sms.dao.submissions;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.entity.submissions.Document;
import edu.neu.ccis.sms.util.HibernateUtil;

/**
 * DAO implementation class for Document Entity bean; Provides access methods for document entity
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 10-June-2015
 */
public class DocumentDaoImpl implements DocumentDao {
    /** Hibernate session instance */
    private Session currentSession;

    /** Hibernate session transaction instance */
    private Transaction currentTransaction;

    /** Default Constructor */
    public DocumentDaoImpl() {
    }

    /**
     * private utility book-keeping method to open hibernate session with a new transaction
     * 
     * @return - Hibernate session instance
     */
    private Session openCurrentSessionwithTransaction() {
        currentSession = HibernateUtil.getSessionFactory().openSession();
        currentTransaction = currentSession.beginTransaction();
        return currentSession;
    }

    /**
     * private utility book-keeping method to close current transaction along with hibernate session, committing any new
     * changes to entities; nulling out old references;
     */
    private void closeCurrentSessionwithTransaction() {
        currentTransaction.commit();
        currentSession.close();
        currentTransaction = null;
        currentSession = null;
    }

    /**
     * Getter method for current active hibernate session, if there isn't any active session then returns null
     * 
     * @return - current active hibernate session instance else null
     */
    public Session getCurrentSession() {
        return currentSession;
    }

    /**
     * Getter method for current active hibernate transaction, if there isn't any active transaction then returns null
     * 
     * @return - current active hibernate transaction instance else null
     */
    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    /**
     * Save a new document
     * 
     * @param newDocument
     *            - new document to be saved
     */
    @Override
    public void saveDocument(Document newDocument) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newDocument);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Update document details for a given document
     * 
     * @param modifiedDocument
     *            - modified document object
     */
    @Override
    public void updateDocument(Document modifiedDocument) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedDocument);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Find a specific document by given document id
     * 
     * @param id
     *            - document id
     * @return - Document obejct with given document id if it exists else null
     */
    public Document findByDocumentId(Long id) {
        openCurrentSessionwithTransaction();
        Document Document = (Document) getCurrentSession().get(Document.class, id);
        closeCurrentSessionwithTransaction();
        return Document;
    }

    /**
     * Delete a document
     * 
     * @param document
     *            - document to be deleted
     */
    @Override
    public void deleteDocument(Document Document) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(Document);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Gets all the documents from all users submitted for all members
     * 
     * @return - list of all documents submitted till now in this system
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Document> getAllDocuments() {
        openCurrentSessionwithTransaction();
        List<Document> categories = (List<Document>) getCurrentSession().createQuery("from Document").list();
        closeCurrentSessionwithTransaction();
        return categories;
    }

    /**
     * Gets a specific document by given document id
     * 
     * @param id
     *            - document id
     * @return - Document obejct with given document id if it exists else null
     */
    @Override
    public Document getDocument(Long id) {
        return findByDocumentId(id);
    }

    /**
     * Load document along with its all evaluations
     * 
     * @param documentId
     *            - id of a document to be retrieved
     * @return - Document object with all its evaluations
     */
    @Override
    public Document getDocumentByIdWithEvaluations(final Long documentId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "select d from Document d left join fetch d.evaluations where d.id = :id");
        query.setParameter("id", documentId);
        List<Document> docs = (List<Document>) query.list();
        closeCurrentSessionwithTransaction();
        if (docs == null || docs.isEmpty()) {
            return null;
        } else {
            return docs.get(0);
        }
    }
}
