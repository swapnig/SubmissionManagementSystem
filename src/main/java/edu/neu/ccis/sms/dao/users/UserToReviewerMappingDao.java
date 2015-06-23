package edu.neu.ccis.sms.dao.users;

import java.util.List;

import edu.neu.ccis.sms.entity.users.UserToReviewerMapping;

/**
 * DAO interface for UserToReviewerMapping Entity bean; 
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface UserToReviewerMappingDao {
    public List<UserToReviewerMapping> getAllUserToReviewerMappings();

    public UserToReviewerMapping getUserToReviewerMapping(Long id);

    public void updateUserToReviewerMapping(UserToReviewerMapping modifiedUserToMemberMapping);

    public void deleteUserToReviewerMapping(UserToReviewerMapping userToMemberMapping);

    public void saveUserToReviewerMapping(UserToReviewerMapping newUserToMemberMapping);

    public void deleteAllUserToReviewerMappingsForMember(final Long memberId);
}
