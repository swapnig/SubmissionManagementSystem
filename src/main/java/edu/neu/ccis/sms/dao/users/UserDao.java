package edu.neu.ccis.sms.dao.users;

import java.util.List;

import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.submissions.Document;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;

/**
 * DAO interface class for User Entity bean; provides access methods for User entity retrievals
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @modifiedBy Swapnil Gupta
 * @lastUpdate 10-June-2015
 */
public interface UserDao {
    /**
     * Gets a list of all users available in current system
     * 
     * @return - list of all users in this system
     */
    public List<User> getAllUser();

    /**
     * Gets a specific User by its user-id which is always unique, if it exists. Else returns null
     * 
     * @param id
     *            - user id
     * @return - User object if there exists a user with a given user-id else returns null
     */
    public User getUser(Long id);

    /**
     * Updates an already existing user
     * 
     * @param modifiedUser
     *            - modified user object to be saved
     */
    public void updateUser(User modifiedUser);

    /**
     * Deletes a user
     * 
     * @param user
     *            - User object to be deleted from system
     */
    public void deleteUser(User user);

    /**
     * Save a new user
     * 
     * @param newUser
     *            - a new user object to saved to persistent storeF
     */
    public void saveUser(User newUser);

    /**
     * Find user by its username, which is also unique per installation of SMS, along with email-id and user-id
     * 
     * @param username
     *            -user name of user to retrieved
     * @return returns User instance else returns null
     */
    public User findUserByUsername(String username);

    /**
     * Find user by its username and password
     * 
     * @param username
     *            - user name string
     * @param password
     *            - MD5 hashed password hex string
     * @return User instance who has given username and password
     */
    public User findUserByUsernameAndPassword(String username, String password);

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
    public UserToMemberMapping registerUserForMember(Long userId, Long memeberId, RoleType role);

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
    public UserToMemberMapping registerUserForMember(User userId, Member memeber, RoleType role);

    /**
     * Find user by email id; user-email id is also unique per installation
     * 
     * @param userEmailId
     *            - email id string of user
     * @return - User object if there exists a user with given email id else returns null
     */
    public User getUserByEmailId(String userEmailId);

    /**
     * Get user by UserId along with all his submission documents (for all members to which this user is registered
     * with)
     * 
     * @param userId
     *            - user id
     * @return User object for given user-id along with all his/her submission-documents
     */
    public User getUserByIdWithSubmissions(final Long userId);

    /**
     * Get User by given userId along with all documents for evaluation for all members where he is registered in
     * EVALUATOR role
     * 
     * @param userId
     *            - user id
     * @return User object with given user-id along with documents to be evaluated by this user - (this has documents
     *         from all members where this user is registered as EVALUATOR)
     */
    public User getUserByIdWithDocumentsForEvaluation(final Long userId);

    /**
     * Get the Submission Document submitted by given userId for given MemberId
     * 
     * @param userId
     *            - id of user who submitted the document
     * @param memberIdToUploadFor
     *            - a submittable member id for which user has submitted the document
     * @return - Document object if user has indeed submitted any document for this member else returns null
     */
    public Document getSubmissionDocumentForMemberIdByUserId(Long userId, Long memberIdToUploadFor);

    /**
     * Get user by its user-id along with his allocated evaluators mappings; i.e. All evaluators who will be grading
     * this user's submissions - UserToReviewerMapping contains details about which document, for which member and by
     * which reviewer user.
     * 
     * @param userId
     *            - user id
     * @return - User object if there exists such user with given user-id else null
     */
    public User getUserByIdWithAllocatedEvaluatorsMappings(final Long userId);

    /**
     * Get user by its user-id along with all his allocated submitters to evaluate; i.e. All submitters whom this user
     * will be grading i.e. UserToReviewerMapping contains details about which document, for which member and by which
     * submitter user.
     * 
     * @param userId
     *            - user id
     * @return - User object if there exists such user with given user-id else null
     */
    public User getUserByIdWithSubmittersToEvaluateMappings(final Long userId);
}
