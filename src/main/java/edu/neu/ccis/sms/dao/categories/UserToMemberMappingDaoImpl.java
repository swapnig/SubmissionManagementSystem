package edu.neu.ccis.sms.dao.categories;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.util.HibernateUtil;

public class UserToMemberMappingDaoImpl implements UserToMemberMappingDao {
    private Session currentSession;
    private Transaction currentTransaction;

    public UserToMemberMappingDaoImpl() {
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

    public void setCurrentSession(final Session currentSession) {
        this.currentSession = currentSession;
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(final Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    @Override
    public void saveUserToMemberMapping(
            final UserToMemberMapping newUserToMemberMapping) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newUserToMemberMapping);
        closeCurrentSessionwithTransaction();
    }

    @Override
    public void updateUserToMemberMapping(
            final UserToMemberMapping modifiedUserToMemberMapping) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedUserToMemberMapping);
        closeCurrentSessionwithTransaction();
    }

    @Override
    public void deleteUserToMemberMapping(
            final UserToMemberMapping userToMemberMapping) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(userToMemberMapping);
        closeCurrentSessionwithTransaction();
    }

    @Override
    public List<UserToMemberMapping> getAllUserToMemberMappings() {
        openCurrentSessionwithTransaction();
        List<UserToMemberMapping> mappings = getCurrentSession()
                .createQuery("from UserToMemberMapping").list();
        closeCurrentSessionwithTransaction();
        return mappings;
    }

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

    public UserToMemberMapping findByUserToMemberMappingId(final Long id) {
        openCurrentSessionwithTransaction();
        UserToMemberMapping mapping = (UserToMemberMapping) getCurrentSession()
                .get(UserToMemberMapping.class, id);
        closeCurrentSessionwithTransaction();
        return mapping;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean doesUserHaveRoleForMember(final Long userId, final RoleType role, final Long memberId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery("from UserToMemberMapping WHERE user_id = :userId AND member_id = :memberId AND role = :role");
        query.setParameter("userId", userId);
        query.setParameter("memberId", memberId);
        query.setParameter("role", role);
        List<UserToMemberMapping> mappings = query.list();
        System.out.println(mappings);
        closeCurrentSessionwithTransaction();
        if (CollectionUtils.isEmpty(mappings)) {
            return false;
        } else {
            return true;
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public List<UserToMemberMapping> getAllUserRolesForMember(final Long userId, final Long memberId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery("from UserToMemberMapping WHERE user_id = :userId AND member_id = :memberId");
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

    public void deleteAll() {
        List<UserToMemberMapping> mappings = getAllUserToMemberMappings();
        for (UserToMemberMapping mapping : mappings) {
            deleteUserToMemberMapping(mapping);
        }
    }

    @Override
    public UserToMemberMapping getUserToMemberMapping(final Long id) {
        return findByUserToMemberMappingId(id);
    }
}
