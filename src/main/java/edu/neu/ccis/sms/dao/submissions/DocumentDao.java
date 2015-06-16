package edu.neu.ccis.sms.dao.submissions;

import java.util.List;

import edu.neu.ccis.sms.entity.submissions.Document;

/**
 * DAO interface for Document Entity bean
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface DocumentDao {
    public List<Document> getAllDocuments();

    public Document getDocument(Long id);

    public void updateDocument(Document modifiedDocument);

    public void deleteDocument(final Document document);

    public void saveDocument(final Document newDocument);

    public Document getDocumentByIdWithEvaluations(final Long documentId);
}
