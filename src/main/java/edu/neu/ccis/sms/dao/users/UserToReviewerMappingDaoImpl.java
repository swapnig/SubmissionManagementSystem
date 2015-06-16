package edu.neu.ccis.sms.dao.users;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.entity.users.UserToReviewerMapping;
import edu.neu.ccis.sms.util.HibernateUtil;

/**
 * DAO implementation class for UserToReviewerMappingDao interface
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public class UserToReviewerMappingDaoImpl implements UserToReviewerMappingDao {
    private Session currentSession;
    private Transaction currentTransaction;

    public UserToReviewerMappingDaoImpl() {
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
    public void saveUserToReviewerMapping(UserToReviewerMapping newUserToReviewerMapping) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newUserToReviewerMapping);
        closeCurrentSessionwithTransaction();
    }

    @Override
    public void updateUserToReviewerMapping(UserToReviewerMapping modifiedUserToReviewerMapping) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedUserToReviewerMapping);
        closeCurrentSessionwithTransaction();
    }

    @Override
    public void deleteUserToReviewerMapping(UserToReviewerMapping UserToReviewerMapping) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(UserToReviewerMapping);
        closeCurrentSessionwithTransaction();
    }

    @Override
    public List<UserToReviewerMapping> getAllUserToReviewerMappings() {
        openCurrentSessionwithTransaction();
        List<UserToReviewerMapping> mappings = (List<UserToReviewerMapping>) getCurrentSession().createQuery(
                "from UserToReviewerMapping").list();
        closeCurrentSessionwithTransaction();
        return mappings;
    }

    public UserToReviewerMapping findByUserToReviewerMappingId(Long id) {
        openCurrentSessionwithTransaction();
        UserToReviewerMapping mapping = (UserToReviewerMapping) getCurrentSession()
                .get(UserToReviewerMapping.class, id);
        closeCurrentSessionwithTransaction();
        return mapping;
    }

    public void deleteAll() {
        List<UserToReviewerMapping> mappings = getAllUserToReviewerMappings();
        for (UserToReviewerMapping mapping : mappings) {
            deleteUserToReviewerMapping(mapping);
        }
    }

    @Override
    public UserToReviewerMapping getUserToReviewerMapping(Long id) {
        return findByUserToReviewerMappingId(id);
    }

    @Override
    public void deleteAllUserToReviewerMappingsForMember(final Long memberId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "from UserToReviewerMapping m where m.evaluationForMemberId = :memberId");
        query.setParameter("memberId", memberId);
        List<UserToReviewerMapping> mappings = (List<UserToReviewerMapping>) query.list();
        closeCurrentSessionwithTransaction();
        if (mappings != null && !mappings.isEmpty()) {
            for (UserToReviewerMapping m : mappings) {
                deleteUserToReviewerMapping(m);
            }
        }
    }
}
