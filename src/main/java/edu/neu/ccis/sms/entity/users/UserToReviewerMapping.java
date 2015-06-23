package edu.neu.ccis.sms.entity.users;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Hibernate Entity bean class for UserToReviewerMapping; Contain user to reviewer mappings i.e. user's submissions will
 * be reviewed by which evaluators, for which submission,which submittable member etc.
 * 
 * @author Pramod R. Khare
 * @date 11-June-2015
 * @lastUpdate 11-June-2015
 */
@Entity
@Table(name = "UserToReviewerMapping", uniqueConstraints = { @UniqueConstraint(columnNames = "ID") })
public class UserToReviewerMapping implements Serializable, Comparable<UserToReviewerMapping> {
    private static final long serialVersionUID = -2755010302418223918L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    /** Submitter who is getting evaluated for his submission document for a particular member */
    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User submitter;

    /** Evaluator who is evaluating submission document from a submitter for a particular member */
    @ManyToOne
    @JoinColumn(name = "REVIEWER_ID", nullable = false)
    private User evaluator;

    /** Member for which submission is done */
    @Column(name = "MEMBER_ID_TO_EVALUATE", nullable = false)
    private Long evaluationForMemberId;

    /** Document to be evaluated */
    @Column(name = "DOCUMENT_ID_TO_EVALUATE")
    private Long evaluateDocId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(User evaluator) {
        this.evaluator = evaluator;
    }

    public Long getEvaluationForMemberId() {
        return evaluationForMemberId;
    }

    public void setEvaluationForMemberId(Long evaluationForMemberId) {
        this.evaluationForMemberId = evaluationForMemberId;
    }

    public Long getEvaluateDocId() {
        return evaluateDocId;
    }

    public void setEvaluateDocId(Long evaluateDocId) {
        this.evaluateDocId = evaluateDocId;
    }

    public User getSubmitter() {
        return submitter;
    }

    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    @Override
    public int compareTo(UserToReviewerMapping o) {
        return this.id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof UserToReviewerMapping) {
            UserToReviewerMapping mappings = (UserToReviewerMapping) anObject;
            return (this.id.equals(mappings.id));
        }
        return false;
    }
}