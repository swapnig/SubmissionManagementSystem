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
 * @lastUpdate 10-June-2015
 */
public interface UserDao {
    /**
     * Gets a list of all users available in current system
     * 
     * @return
     */
    public List<User> getAllUser();

    /**
     * Gets a specific User by its user-id which is always unique, if it exists. Else returns null
     * 
     * @param id
     * @return
     */
    public User getUser(Long id);

    /**
     * Updates an already existing user
     * 
     * @param modifiedUser
     */
    public void updateUser(User modifiedUser);

    /**
     * Deletes a user
     * 
     * @param user
     */
    public void deleteUser(User user);

    /**
     * Save a new user
     * 
     * @param newUser
     */
    public void saveUser(User newUser);

    /**
     * Find user by its username, which also unique per installation of SMS
     * 
     * @param username
     * @return returns User instance else returns null
     */
    public User findUserByUsername(String username);

    /**
     * Find user by its username and password
     * 
     * @param username
     * @param password
     * @return
     */
    public User findUserByUsernameAndPassword(String username, String password);

    /**
     * Register user for a member
     * 
     * @param userId
     * @param memeberId
     * @param role
     * @return returns the new user registration mapping
     */
    public UserToMemberMapping registerUserForMember(Long userId, Long memeberId, RoleType role);

    /**
     * Overloaded method to register a user for a member
     * 
     * @param userId
     * @param memeber
     * @param role
     * @return
     */
    public UserToMemberMapping registerUserForMember(User userId, Member memeber, RoleType role);

    /**
     * Find user by email id; user-email id is also unique per installation
     * 
     * @param userEmailId
     * @return
     */
    public User getUserByEmailId(String userEmailId);

    /**
     * Get user by UserId along with all his submissions
     * 
     * @param userId
     * @return
     */
    public User getUserByIdWithSubmissions(final Long userId);

    /**
     * Get user by UserId along with all documents for evaluation
     * 
     * @param userId
     * @return
     */
    public User getUserByIdWithDocumentsForEvaluation(final Long userId);

    /**
     * Get the Submission Document reference by userId for given MemberId
     * 
     * @param userId
     * @param memberIdToUploadFor
     * @return
     */
    public Document getSubmissionDocumentForMemberIdByUserId(Long userId, Long memberIdToUploadFor);

    /**
     * Get user by its user-id along with his allocated evaluators mappings; i.e. All evaluators who will be grading
     * this user's submissions - UserToReviewerMapping contains details about which document, for which member and by
     * which reviewer user.
     * 
     * @param userId
     * @return
     */
    public User getUserByIdWithAllocatedEvaluatorsMappings(final Long userId);

    /**
     * Get user by its user-id along with all his allocated submitters to evaluate; i.e. All submitters whom this user
     * will be grading i.e. UserToReviewerMapping contains details about which document, for which member and by which
     * submitter user.
     * 
     * @param userId
     * @return
     */
    public User getUserByIdWithSubmittersToEvaluateMappings(final Long userId);
}
