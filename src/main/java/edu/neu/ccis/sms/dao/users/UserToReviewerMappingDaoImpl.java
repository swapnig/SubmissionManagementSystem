package edu.neu.ccis.sms.dao.users;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
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
    /** Hibernate session instance */
    private Session currentSession;

    /** Hibernate session transaction instance */
    private Transaction currentTransaction;

    /** Default Constructor */
    public UserToReviewerMappingDaoImpl() {
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
     * Save new user to reviewer mapping object
     * 
     * @param newUserToMemberMapping
     *            - new user to reviewer mapping object
     */
    @Override
    public void saveUserToReviewerMapping(UserToReviewerMapping newUserToReviewerMapping) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newUserToReviewerMapping);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Update an already existing user-to-reviewer mapping
     * 
     * @param modifiedUserToMemberMapping
     *            - modified user-to-reviewer mapping object
     */
    @Override
    public void updateUserToReviewerMapping(UserToReviewerMapping modifiedUserToReviewerMapping) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedUserToReviewerMapping);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Delete a user-to-reviewer mapping from system, if it exists else does nothing
     * 
     * @param userToMemberMapping
     *            - mapping object to delete
     */
    @Override
    public void deleteUserToReviewerMapping(UserToReviewerMapping UserToReviewerMapping) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(UserToReviewerMapping);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Get All User to reviewer mappings for all users
     * 
     * @return - list of all user-to-reviewer mappings
     */
    @Override
    public List<UserToReviewerMapping> getAllUserToReviewerMappings() {
        openCurrentSessionwithTransaction();
        List<UserToReviewerMapping> mappings = (List<UserToReviewerMapping>) getCurrentSession().createQuery(
                "from UserToReviewerMapping").list();
        closeCurrentSessionwithTransaction();
        return mappings;
    }

    /**
     * Find user to reviewer mapping by its mapping id
     * 
     * @param id
     *            - id of user to reviewer mapping
     * @return - UserToReviewerMapping instance for given mapping-id if it exists, else null
     */
    public UserToReviewerMapping findByUserToReviewerMappingId(Long id) {
        openCurrentSessionwithTransaction();
        UserToReviewerMapping mapping = (UserToReviewerMapping) getCurrentSession()
                .get(UserToReviewerMapping.class, id);
        closeCurrentSessionwithTransaction();
        return mapping;
    }

    /**
     * Find user to reviewer mapping by its mapping id
     * 
     * @param id
     *            - id of user to reviewer mapping
     * @return - UserToReviewerMapping instance for given mapping-id if it exists, else null
     */
    @Override
    public UserToReviewerMapping getUserToReviewerMapping(Long id) {
        return findByUserToReviewerMappingId(id);
    }

    /**
     * Delete all user-to-reviewer mappings for given member; this method should be used when clearing any previous
     * existing reviewers allocations
     * 
     * @param memberId
     *            - member id for which all previous user-to-reviewer mapping to be deleted
     */
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
