package edu.neu.ccis.sms.dao.users;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.dao.categories.MemberDao;
import edu.neu.ccis.sms.dao.categories.MemberDaoImpl;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDao;
import edu.neu.ccis.sms.dao.categories.UserToMemberMappingDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.submissions.Document;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;
import edu.neu.ccis.sms.util.HibernateUtil;

/**
 * DAO implementation class for User Entity bean; Provides access methods for User entity
 * 
 * @author Pramod R. Khare, Swapnil Gupta
 * @date 9-May-2015
 * @modifiedBy
 * @lastUpdate 10-June-2015
 */
public class UserDaoImpl implements UserDao {
    /** Hibernate session instance */
    private Session currentSession;

    /** Hibernate session transaction instance */
    private Transaction currentTransaction;

    /** Default Constructor */
    public UserDaoImpl() {
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
     * Save a new user
     * 
     * @param newUser
     *            - a new user object to saved to persistent storeF
     */
    @Override
    public void saveUser(final User newUser) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newUser);
        closeCurrentSessionwithTransaction();
        ;
    }

    /**
     * Updates an already existing user
     * 
     * @param modifiedUser
     *            - modified user object to be saved
     */
    @Override
    public void updateUser(final User modifiedUser) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedUser);
        closeCurrentSessionwithTransaction();
        ;
    }

    /**
     * Finds a specific User by its user-id which is always unique, if it exists. Else returns null
     * 
     * @param id
     *            - user id
     * @return - User object if there exists a user with a given user-id else returns null
     */
    public User findByUserId(final Long id) {
        openCurrentSessionwithTransaction();
        User user = (User) getCurrentSession().get(User.class, id);
        closeCurrentSessionwithTransaction();
        return user;
    }

    /**
     * Deletes a user
     * 
     * @param user
     *            - User object to be deleted from system
     */
    @Override
    public void deleteUser(final User user) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(user);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Gets a list of all users available in current system
     * 
     * @return - list of all users in this system
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<User> getAllUser() {
        openCurrentSessionwithTransaction();
        List<User> users = getCurrentSession().createQuery("from User").list();
        closeCurrentSessionwithTransaction();
        return users;
    }

    /**
     * Gets a specific User by its user-id which is always unique, if it exists. Else returns null
     * 
     * @param id
     *            - user id
     * @return - User object if there exists a user with a given user-id else returns null
     */
    @Override
    public User getUser(final Long id) {
        return findByUserId(id);
    }

    /**
     * Find user by its username, which is also unique per installation of SMS, along with email-id and user-id
     * 
     * @param username
     *            -user name of user to retrieved
     * @return returns User instance else returns null
     */
    @Override
    public User findUserByUsername(final String username) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery("from User WHERE username = :username");
        query.setParameter("username", username);
        List<User> users = query.list();
        closeCurrentSessionwithTransaction();
        if (users == null || users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    /**
     * Find user by its username and password
     * 
     * @param username
     *            - user name string
     * @param password
     *            - MD5 hashed password hex string
     * @return User instance who has given username and password
     */
    @Override
    public User findUserByUsernameAndPassword(final String username, final String password) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery("from User WHERE username = :username AND password = :password");
        query.setParameter("username", username);
        query.setParameter("password", password);

        List<User> users = query.list();
        closeCurrentSessionwithTransaction();
        if (users == null || users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    /**
     * Overloaded method to register a user for a member in given RoleType role
     * 
     * @param userId
     *            - user id
     * @param memeber
     *            - member id
     * @param role
     *            - RoleType role which given user will assume for given member
     * @return - User-to-member registration mapping for given user and member in given role
     */
    @Override
    public UserToMemberMapping registerUserForMember(final User user, final Member member, final RoleType role) {
        UserToMemberMapping mapping = new UserToMemberMapping();
        mapping.setMember(member);
        mapping.setUser(user);
        mapping.setActive(true);
        mapping.setRole(role);
        UserToMemberMappingDao mappingDao = new UserToMemberMappingDaoImpl();
        mappingDao.saveUserToMemberMapping(mapping);

        return mapping;
    }

    /**
     * Register user for a member in given role
     * 
     * @param userId
     *            - id of user who is getting registered
     * @param memeberId
     *            - member id
     * @param role
     *            - RoleType role
     * @return returns the new user-to-member registration mapping
     */
    @Override
    public UserToMemberMapping registerUserForMember(final Long userId, final Long memeberId, final RoleType role) {
        User u1 = getUser(userId);

        MemberDao memDao = new MemberDaoImpl();
        Member m1 = memDao.getMember(memeberId);

        UserToMemberMapping mapping = new UserToMemberMapping();
        mapping.setMember(m1);
        mapping.setUser(u1);
        mapping.setActive(true);
        mapping.setRole(role);

        UserToMemberMappingDao mappingDao = new UserToMemberMappingDaoImpl();
        mappingDao.saveUserToMemberMapping(mapping);

        return mapping;
    }

    /**
     * Find user by email id; user-email id is also unique per installation
     * 
     * @param userEmailId
     *            - email id string of user
     * @return - User object if there exists a user with given email id else returns null
     */
    @Override
    public User getUserByEmailId(final String userEmailId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery("from User WHERE email = :email");
        query.setParameter("email", userEmailId);
        List<User> users = query.list();
        closeCurrentSessionwithTransaction();
        if (users == null || users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    /**
     * Get user by UserId along with all his submission documents (for all members to which this user is registered
     * with)
     * 
     * @param userId
     *            - user id
     * @return User object for given user-id along with all his/her submission-documents
     */
    @Override
    public User getUserByIdWithSubmissions(final Long userId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "select u from User u left join fetch u.submissions where u.id = :id");
        query.setParameter("id", userId);
        List<User> users = query.list();
        closeCurrentSessionwithTransaction();
        if (users == null || users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    /**
     * Get the Submission Document submitted by given userId for given MemberId
     * 
     * @param userId
     *            - id of user who submitted the document
     * @param memberIdToUploadFor
     *            - a submittable member id for which user has submitted the document
     * @return - Document object if user has indeed submitted any document for this member else returns null
     */
    @Override
    public Document getSubmissionDocumentForMemberIdByUserId(final Long userId, final Long memberIdToUploadFor) {
        User user = getUserByIdWithSubmissions(userId);
        if (user == null) {
            return null;
        }
        for (Document submission : user.getSubmissions()) {
            if (memberIdToUploadFor.equals(submission.getSubmittedForMember().getId())) {
                return submission;
            }
        }
        return null;
    }

    /**
     * Get User by given userId along with all documents for evaluation for all members where he is registered in
     * EVALUATOR role
     * 
     * @param userId
     *            - user id
     * @return User object with given user-id along with documents to be evaluated by this user - (this has documents
     *         from all members where this user is registered as EVALUATOR)
     */
    @Override
    public User getUserByIdWithDocumentsForEvaluation(final Long userId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "select u from User u left join fetch u.documentsForEvaluation where u.id = :id");
        query.setParameter("id", userId);
        List<User> users = query.list();
        closeCurrentSessionwithTransaction();
        if (users == null || users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    /**
     * Get user by its user-id along with his allocated evaluators mappings; i.e. All evaluators who will be grading
     * this user's submissions - UserToReviewerMapping contains details about which document, for which member and by
     * which reviewer user.
     * 
     * @param userId
     *            - user id
     * @return - User object if there exists such user with given user-id else null
     */
    @Override
    public User getUserByIdWithAllocatedEvaluatorsMappings(final Long userId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "select u from User u left join fetch u.allocatedEvaluators where u.id = :id");
        query.setParameter("id", userId);
        List<User> users = query.list();
        closeCurrentSessionwithTransaction();
        if (users == null || users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }

    /**
     * Get user by its user-id along with all his allocated submitters to evaluate; i.e. All submitters whom this user
     * will be grading i.e. UserToReviewerMapping contains details about which document, for which member and by which
     * submitter user.
     * 
     * @param userId
     *            - user id
     * @return - User object if there exists such user with given user-id else null
     */
    @Override
    public User getUserByIdWithSubmittersToEvaluateMappings(final Long userId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "select u from User u left join fetch u.submittersToEvaluate where u.id = :id");
        query.setParameter("id", userId);
        List<User> users = query.list();
        closeCurrentSessionwithTransaction();
        if (users == null || users.isEmpty()) {
            return null;
        } else {
            return users.get(0);
        }
    }
}