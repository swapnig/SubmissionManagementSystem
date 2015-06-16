package edu.neu.ccis.sms.dao.submissions;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.entity.submissions.Evaluation;
import edu.neu.ccis.sms.util.HibernateUtil;

public class EvaluationDaoImpl implements EvaluationDao {
    private Session currentSession;
    private Transaction currentTransaction;

    public EvaluationDaoImpl() {
    }

    public Session openCurrentSessionwithTransaction() {
        currentSession = getSessionFactory().openSession();
        currentTransaction = currentSession.beginTransaction();
        return currentSession;
    }

    public void closeCurrentSessionwithTransaction() {
        currentTransaction.commit();
        currentSession.close();
    }

    private static SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    @Override
    public void saveEvaluation(Evaluation newEvaluation) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newEvaluation);
        closeCurrentSessionwithTransaction();
    }

    @Override
    public void updateEvaluation(Evaluation modifiedEvaluation) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedEvaluation);
        closeCurrentSessionwithTransaction();
    }

    public Evaluation findByEvaluationId(Long id) {
        openCurrentSessionwithTransaction();
        Evaluation Evaluation = (Evaluation) getCurrentSession().get(Evaluation.class,
                id);
        closeCurrentSessionwithTransaction();
        return Evaluation;
    }

    @Override
    public void deleteEvaluation(Evaluation Evaluation) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(Evaluation);
        closeCurrentSessionwithTransaction();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Evaluation> getAllEvaluations() {
        openCurrentSessionwithTransaction();
        List<Evaluation> categories = (List<Evaluation>) getCurrentSession()
                .createQuery("from Evaluation").list();
        closeCurrentSessionwithTransaction();
        return categories;
    }

    public void deleteAllCategories() {
        List<Evaluation> EvaluationList = getAllEvaluations();
        for (Evaluation evaluation : EvaluationList) {
            deleteEvaluation(evaluation);
        }
    }

    @Override
    public Evaluation getEvaluation(Long id) {
        return findByEvaluationId(id);
    }
}
