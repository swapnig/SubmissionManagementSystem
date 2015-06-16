package edu.neu.ccis.sms.entity.submissions;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.users.User;

@Entity
@Table(name = "Document", uniqueConstraints = { @UniqueConstraint(columnNames = "DOCUMENT_ID") })
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DOCUMENT_ID", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member submittedForMember;

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(name = "Document_submission_mapping", 
        joinColumns = { 
            @JoinColumn(name = "DOCUMENT_ID") }, 
            inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
    private Set<User> submittedBy = new HashSet<User>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "evaluationFor")
    private Set<Evaluation> evaluations = new HashSet<Evaluation>();

    @Column(name = "SUBMITTED_FROM")
    private String submittedFromRemoteAddress;

    @Column(name = "SUBMITTED_ON")
    private long submittedOnTimestamp;

    @Column(name = "FILENAME")
    private String filename;

    @Column(name = "CONTENTTYPE")
    private String contentType;

    @Column(name = "CMS_DOC_ID")
    private String cmsDocId;

    @Column(name = "CMS_DOC_PATH")
    private String cmsDocumentPath;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<Evaluation> getEvaluations() {
        return evaluations;
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

    public long getSubmittedOnTimestamp() {
        return submittedOnTimestamp;
    }

    public void setSubmittedOnTimestamp(long submittedOnTimestamp) {
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
}
