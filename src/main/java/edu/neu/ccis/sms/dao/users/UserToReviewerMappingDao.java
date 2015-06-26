package edu.neu.ccis.sms.dao.users;

import java.util.List;

import edu.neu.ccis.sms.entity.users.UserToReviewerMapping;

/**
 * DAO interface for UserToReviewerMapping Entity bean; Provides methods for accessing user to revie
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface UserToReviewerMappingDao {
    /**
     * Get All User to reviewer mappings for all users
     * 
     * @return - list of all user-to-reviewer mappings
     */
    public List<UserToReviewerMapping> getAllUserToReviewerMappings();

    /**
     * Find user to reviewer mapping by its mapping id
     * 
     * @param id
     *            - id of user to reviewer mapping
     * @return - UserToReviewerMapping instance for given mapping-id if it exists, else null
     */
    public UserToReviewerMapping getUserToReviewerMapping(Long id);

    /**
     * Update an already existing user-to-reviewer mapping
     * 
     * @param modifiedUserToMemberMapping
     *            - modified user-to-reviewer mapping object
     */
    public void updateUserToReviewerMapping(UserToReviewerMapping modifiedUserToMemberMapping);

    /**
     * Delete a user-to-reviewer mapping from system, if it exists else does nothing
     * 
     * @param userToMemberMapping
     *            - mapping object to delete
     */
    public void deleteUserToReviewerMapping(UserToReviewerMapping userToMemberMapping);

    /**
     * Save new user to reviewer mapping object
     * 
     * @param newUserToMemberMapping
     *            - new user to reviewer mapping object
     */
    public void saveUserToReviewerMapping(UserToReviewerMapping newUserToMemberMapping);

    /**
     * Delete all user-to-reviewer mappings for given member; this method should be used when clearing any previous
     * existing reviewers allocations
     * 
     * @param memberId
     *            - member id for which all previous user-to-reviewer mapping to be deleted
     */
    public void deleteAllUserToReviewerMappingsForMember(final Long memberId);
}
