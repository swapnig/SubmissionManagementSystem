package edu.neu.ccis.sms.dao.categories;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.util.HibernateUtil;

/**
 * DAO implementation class for UserToMemberMapping entity; This DaoImpl class provides access methods for
 * user-to-member registration entities
 * 
 * @author Pramod R. Khare, Swapnil Gupta
 * @date 9-May-2015
 */
public class UserToMemberMappingDaoImpl implements UserToMemberMappingDao {
    /** Hibernate session instance */
    private Session currentSession;

    /** Hibernate session transaction instance */
    private Transaction currentTransaction;

    /** Default Constructor */
    public UserToMemberMappingDaoImpl() {
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
     * Save a new user to member registration
     * 
     * @param newUserToMemberMapping
     *            - new registration details object
     */
    @Override
    public void saveUserToMemberMapping(final UserToMemberMapping newUserToMemberMapping) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newUserToMemberMapping);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Update already existing user to member registration details mapping
     * 
     * @param modifiedUserToMemberMapping
     *            - user to member registration mapping to be modified
     */
    @Override
    public void updateUserToMemberMapping(final UserToMemberMapping modifiedUserToMemberMapping) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedUserToMemberMapping);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Delete a user to member registration mapping
     * 
     * @param userToMemberMapping
     *            - user to member registration object to be deleted
     */
    @Override
    public void deleteUserToMemberMapping(final UserToMemberMapping userToMemberMapping) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(userToMemberMapping);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Get all user to member registrations for all available members;
     * 
     * @return - list of all user-to-member registration mappings
     */
    @Override
    public List<UserToMemberMapping> getAllUserToMemberMappings() {
        openCurrentSessionwithTransaction();
        List<UserToMemberMapping> mappings = getCurrentSession().createQuery("from UserToMemberMapping").list();
        closeCurrentSessionwithTransaction();
        return mappings;
    }

    /**
     * Get all members to which given user id has registrations with (including all registration statuses, in all
     * registration role types)
     * 
     * @param userId
     *            - user id
     * @return - list of user-to-member registrations for a given user
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<UserToMemberMapping> getAllMembersForUser(final Long userId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery("from UserToMemberMapping WHERE user_id = :userId ORDER BY role");
        query.setParameter("userId", userId);

        List<UserToMemberMapping> mappings = query.list();
        closeCurrentSessionwithTransaction();
        return mappings;
    }

    /**
     * Get all registerable members for which given user has registered with
     * 
     * @param userId
     *            - id of user
     * @return - list of mappings for which given user has registered with some kind of role
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<UserToMemberMapping> getAllRegisterableMembersForUser(final Long userId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery("from UserToMemberMapping WHERE user_id = :userId ORDER BY role");
        query.setParameter("userId", userId);

        List<UserToMemberMapping> mappings = query.list();
        closeCurrentSessionwithTransaction();
        return mappings;
    }

    /**
     * Find member registration (User to member mapping) details for a given registration id
     * 
     * @param id
     *            - registration mapping id
     * @return - User to member registration mapping for given registration mapping id
     */
    public UserToMemberMapping findByUserToMemberMappingId(final Long id) {
        openCurrentSessionwithTransaction();
        UserToMemberMapping mapping = (UserToMemberMapping) getCurrentSession().get(UserToMemberMapping.class, id);
        closeCurrentSessionwithTransaction();
        return mapping;
    }

    /**
     * Checks if given user has a registration for a given member in given specific role
     * 
     * @param userId
     *            - user id
     * @param role
     *            - role
     * @param memberId
     *            - member id
     * @return - boolean result if user has registered with given member with given role
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean doesUserHaveRoleForMember(final Long userId, final RoleType role, final Long memberId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "from UserToMemberMapping WHERE user_id = :userId AND member_id = :memberId AND role = :role");
        query.setParameter("userId", userId);
        query.setParameter("memberId", memberId);
        query.setParameter("role", role);
        List<UserToMemberMapping> mappings = query.list();
        closeCurrentSessionwithTransaction();
        if (CollectionUtils.isEmpty(mappings)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Get user's role for given member registration, if user is not registered with given member it will return null;
     * If user has multiple roles registered with given member then returns its first role
     * 
     * @param userId
     *            - user id
     * @param memberId
     *            - member id
     * @return - RoleType if given user is registered with given member else returns null
     */
    @SuppressWarnings("unchecked")
    @Override
    public RoleType getUsersRoleForMember(final Long userId, final Long memberId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "from UserToMemberMapping WHERE user_id = :userId AND member_id = :memberId");
        query.setParameter("userId", userId);
        query.setParameter("memberId", memberId);
        List<UserToMemberMapping> mappings = query.list();
        closeCurrentSessionwithTransaction();
        if (CollectionUtils.isEmpty(mappings)) {
            return null;
        } else {
            return mappings.get(0).getRole();
        }
    }

    /**
     * Get all roles a given user has assumed for a given member
     * 
     * @param userId
     *            - id of user
     * @param memberId
     *            - id of member
     * @return - list of role mappings that given user have for given member
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<UserToMemberMapping> getAllUserRolesForMember(final Long userId, final Long memberId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "from UserToMemberMapping WHERE user_id = :userId AND member_id = :memberId");
        query.setParameter("userId", userId);
        query.setParameter("memberId", memberId);
        List<UserToMemberMapping> mappings = query.list();
        closeCurrentSessionwithTransaction();
        if (CollectionUtils.isEmpty(mappings)) {
            return null;
        } else {
            return mappings;
        }

    }

    /**
     * Get member registration (User to member mapping) details for a given registration id
     * 
     * @param id
     *            - registration mapping id
     * @return - User to member registration mapping for given registration mapping id
     */
    @Override
    public UserToMemberMapping getUserToMemberMapping(final Long id) {
        return findByUserToMemberMappingId(id);
    }
}
