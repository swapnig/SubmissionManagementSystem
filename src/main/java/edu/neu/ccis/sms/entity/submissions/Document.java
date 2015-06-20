package edu.neu.ccis.sms.entity.submissions;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.users.User;

/**
 * Hibernate Entity bean class for Document; Encompasses a submission done by user, resubmissions for a given member
 * just update the old Document instance
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
@Entity
@Table(name = "Document", uniqueConstraints = { @UniqueConstraint(columnNames = "DOCUMENT_ID") })
public class Document implements Serializable, Comparable<Document> {
    private static final long serialVersionUID = 1407366847843408348L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DOCUMENT_ID", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member submittedForMember;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "DocumentSubmissionMapping", joinColumns = { @JoinColumn(name = "DOCUMENT_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
    private Set<User> submittedBy = new HashSet<User>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "DocumentEvaluatorsMapping", joinColumns = { @JoinColumn(name = "DOCUMENT_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
    private Set<User> evaluators = new HashSet<User>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "evaluationFor")
    private Set<Evaluation> evaluations = new HashSet<Evaluation>();

    // Unidiretional one-to-one mapping for final Evaluation of this document
    // Final Evaluation of document can be average of multiple evaluations done
    // by multiple reviewers or evaluators
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "FINAL_EVAL_ID", unique = true, nullable = true, insertable = true, updatable = true)
    private Evaluation finalEvaluation;

    @Column(name = "SUBMITTED_FROM")
    private String submittedFromRemoteAddress;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SUBMITTED_ON", nullable = false)
    private Date submittedOnTimestamp;

    @Column(name = "FILENAME")
    private String filename;

    @Column(name = "CONTENTTYPE")
    private String contentType;

    @Column(name = "CMS_DOC_ID")
    private String cmsDocId;

    @Column(name = "CMS_DOC_CONTENT_URL")
    private String cmsDocContentUrl;

    @Column(name = "CMS_DOC_PATH")
    private String cmsDocumentPath;

    @Column(name = "CMS_DOC_VERSION")
    private String cmsDocVersion;

    /* Saving the submittedOnTimestamp by default */
    public Document() {
        submittedOnTimestamp = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCmsDocVersion() {
        return cmsDocVersion;
    }

    public void setCmsDocVersion(String cmsDocVersion) {
        this.cmsDocVersion = cmsDocVersion;
    }

    public String getCmsDocContentUrl() {
        return cmsDocContentUrl;
    }

    public void setCmsDocContentUrl(String cmsDocContentUrl) {
        this.cmsDocContentUrl = cmsDocContentUrl;
    }

    public Evaluation getFinalEvaluation() {
        return finalEvaluation;
    }

    public void setFinalEvaluation(Evaluation finalEvaluation) {
        this.finalEvaluation = finalEvaluation;
    }

    public Member getSubmittedForMember() {
        return submittedForMember;
    }

    public void setSubmittedForMember(Member submittedForMember) {
        this.submittedForMember = submittedForMember;
    }

    public Set<User> getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(Set<User> submittedBy) {
        this.submittedBy = submittedBy;
    }

    public boolean addSubmittedBy(User submittedBy) {
        return this.submittedBy.add(submittedBy);
    }

    public Set<Evaluation> getEvaluations() {
        return evaluations;
    }

    public boolean addEvaluation(Evaluation evaluation) {
        return evaluations.add(evaluation);
    }

    public void setEvaluations(Set<Evaluation> evaluations) {
        this.evaluations = evaluations;
    }

    public String getSubmittedFromRemoteAddress() {
        return submittedFromRemoteAddress;
    }

    public void setSubmittedFromRemoteAddress(String submittedFromRemoteAddress) {
        this.submittedFromRemoteAddress = submittedFromRemoteAddress;
    }

    public Date getSubmittedOnTimestamp() {
        return submittedOnTimestamp;
    }

    public void setSubmittedOnTimestamp(Date submittedOnTimestamp) {
        this.submittedOnTimestamp = submittedOnTimestamp;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCmsDocId() {
        return cmsDocId;
    }

    public void setCmsDocId(String cmsDocId) {
        this.cmsDocId = cmsDocId;
    }

    public String getCmsDocumentPath() {
        return cmsDocumentPath;
    }

    public void setCmsDocumentPath(String cmsDocumentPath) {
        this.cmsDocumentPath = cmsDocumentPath;
    }

    public Set<User> getEvaluators() {
        return evaluators;
    }

    public void setEvaluators(Set<User> evaluators) {
        this.evaluators = evaluators;
    }

    public boolean addEvaluator(User evaluator) {
        return this.evaluators.add(evaluator);
    }

    @Override
    public int compareTo(Document o) {
        return this.id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Document) {
            Document anotherDoc = (Document) anObject;
            return (this.id.equals(anotherDoc.id));
        }
        return false;
    }
}
