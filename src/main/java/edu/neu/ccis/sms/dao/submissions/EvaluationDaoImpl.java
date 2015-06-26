package edu.neu.ccis.sms.dao.submissions;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.entity.submissions.Evaluation;
import edu.neu.ccis.sms.util.HibernateUtil;

/**
 * DAO implementation class for Evaluation Entity bean; Provides access methods for evaluation entities
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 10-June-2015
 */
public class EvaluationDaoImpl implements EvaluationDao {
    /** Hibernate session instance */
    private Session currentSession;

    /** Hibernate session transaction instance */
    private Transaction currentTransaction;

    /** Default Constructor */
    public EvaluationDaoImpl() {
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
     * Save a new evaluation
     * 
     * @param newEvaluation
     *            - new evaluation object to be saved
     */
    @Override
    public void saveEvaluation(Evaluation newEvaluation) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newEvaluation);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Update evaluation details
     * 
     * @param modifiedEvaluation
     *            - modified evaluation object
     */
    @Override
    public void updateEvaluation(Evaluation modifiedEvaluation) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedEvaluation);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Find a specific evaluation by its evaluation id
     * 
     * @param id
     *            - evaluation entity id
     * @return - Evaluation object for given evaluation id if it exists else returns null
     */
    public Evaluation findByEvaluationId(Long id) {
        openCurrentSessionwithTransaction();
        Evaluation Evaluation = (Evaluation) getCurrentSession().get(Evaluation.class, id);
        closeCurrentSessionwithTransaction();
        return Evaluation;
    }

    /**
     * Delete existing evaluation
     * 
     * @param evaluation
     *            - evaluation object to be deleted
     */
    @Override
    public void deleteEvaluation(Evaluation Evaluation) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(Evaluation);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Gets all evaluations for all documents submitted in current system
     * 
     * @return - list of all evaluations done till now in this system
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Evaluation> getAllEvaluations() {
        openCurrentSessionwithTransaction();
        List<Evaluation> categories = (List<Evaluation>) getCurrentSession().createQuery("from Evaluation").list();
        closeCurrentSessionwithTransaction();
        return categories;
    }

    /**
     * Get a specific evaluation by its evaluation id
     * 
     * @param id
     *            - evaluation entity id
     * @return - Evaluation object for given evaluation id if it exists else returns null
     */
    @Override
    public Evaluation getEvaluation(Long id) {
        return findByEvaluationId(id);
    }
}
