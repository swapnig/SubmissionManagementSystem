package edu.neu.ccis.sms.dao.submissions;

import java.util.List;

import edu.neu.ccis.sms.entity.submissions.Document;

public interface DocumentDao {
    public List<Document> getAllDocuments();

    public Document getDocument(Long id);

    public void updateDocument(Document modifiedDocument);

    public void deleteDocument(Document document);

    public void saveDocument(Document newDocument);
}
