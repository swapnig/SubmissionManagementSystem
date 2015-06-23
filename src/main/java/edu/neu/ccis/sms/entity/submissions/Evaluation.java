package edu.neu.ccis.sms.entity.submissions;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import edu.neu.ccis.sms.entity.users.User;

/**
 * Hibernate Entity bean class for Evaluation; contains evaluation results for a
 * Document by a single reviewer
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
@Entity
@Table(name = "Evaluation", uniqueConstraints = { @UniqueConstraint(columnNames = "EVALUATION_ID") })
public class Evaluation implements Serializable, Comparable<Evaluation> {
    private static final long serialVersionUID = -8527463008215351943L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVALUATION_ID", unique = true, nullable = false)
    private Long id;

    /** Evaluation received */
    @Column(name = "RESULT", nullable = false)
    private Float result;

    /** Total maximum points possible */
    @Column(name = "TOTAL", nullable = false)
    private Float outOfTotal;

    /** Comments received for this evaluation */
    @Column(name = "COMMENTS", nullable = true)
    private String comments;

    /** Evaluation submitted on */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "EVALUATED_ON", nullable = false)
    private Date evaluatedOnTimestamp;

    /** Evaluator who evaluated submission */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User evaluatedBy;

    /** Document which is evaluated */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCUMENT_ID", nullable = true)
    private Document evaluationFor;

    /** Default Constructor - Evaluation submitted on is populated to current time */
    public Evaluation(){
        evaluatedOnTimestamp = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getResult() {
        return result;
    }

    public void setResult(Float result) {
        this.result = result;
    }

    public Float getOutOfTotal() {
        return outOfTotal;
    }

    public void setOutOfTotal(Float outOfTotal) {
        this.outOfTotal = outOfTotal;
    }

    public Date getEvaluatedOnTimestamp() {
        return evaluatedOnTimestamp;
    }

    public void setEvaluatedOnTimestamp(Date evaluatedOnTimestamp) {
        this.evaluatedOnTimestamp = evaluatedOnTimestamp;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public User getEvaluatedBy() {
        return evaluatedBy;
    }

    public void setEvaluatedBy(User evaluatedBy) {
        this.evaluatedBy = evaluatedBy;
    }

    public Document getEvaluationFor() {
        return evaluationFor;
    }

    public void setEvaluationFor(Document evaluationFor) {
        this.evaluationFor = evaluationFor;
    }

    @Override
    public int compareTo(Evaluation o) {
        return this.id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Evaluation) {
            Evaluation anotherEval = (Evaluation) anObject;
            return (this.id.equals(anotherEval.id) && this.evaluatedBy.getId().equals(anotherEval.evaluatedBy.getId()) && this.evaluationFor
                    .getId().equals(anotherEval.evaluationFor.getId()));
        }
        return false;
    }
}
