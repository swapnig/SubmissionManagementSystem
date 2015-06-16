package edu.neu.ccis.sms.dao.users;

import java.util.List;
import java.util.Set;

import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.submissions.Document;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;

/**
 * DAO interface class for User Entity bean
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 10-June-2015
 */
public interface UserDao {
    public List<User> getAllUser();

    public User getUser(Long id);

    public void updateUser(User modifiedUser);

    public void deleteUser(User user);

    public void saveUser(User newUser);

    // find user by username
    public User findUserByUsername(String username);

    // find user by username and password
    public User findUserByUsernameAndPassword(String username, String password);

    // register for member
    public UserToMemberMapping registerUserForMember(Long userId, Long memeberId, RoleType role);

    public UserToMemberMapping registerUserForMember(User userId, Member memeber, RoleType role);

    /* find user by email id */
    public User getUserByEmailId(String userEmailId);

    /* Get user by UserId along with all his submissions */
    public User getUserByIdWithSubmissions(final Long userId);

    /* Get user by UserId along with all documents for evaluation */
    public User getUserByIdWithDocumentsForEvaluation(final Long userId);

    /* Get the Submission Document reference by userId for given MemberId */
    public Document getSubmissionDocumentForMemberIdByUserId(Long userId, Long memberIdToUploadFor);

    public User getUserByIdWithAllocatedEvaluatorsMappings(final Long userId);

    public User getUserByIdWithSubmittersToEvaluateMappings(final Long userId);
}
