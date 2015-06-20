package edu.neu.ccis.sms.dao.categories;

import java.util.List;

import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.users.RoleType;

public interface UserToMemberMappingDao {
    // Member
    public List<UserToMemberMapping> getAllUserToMemberMappings();

    public UserToMemberMapping getUserToMemberMapping(Long id);

    public void updateUserToMemberMapping(UserToMemberMapping modifiedUserToMemberMapping);

    public void deleteUserToMemberMapping(UserToMemberMapping userToMemberMapping);

    public void saveUserToMemberMapping(UserToMemberMapping newUserToMemberMapping);

	public List<UserToMemberMapping> getAllMembersForUser(Long userId);

	public boolean doesUserHaveRoleForMember(Long userId, RoleType role, Long memberId);

	public RoleType getUsersRoleForMember(final Long userId, final Long memberId);

	public List<UserToMemberMapping> getAllRegisterableMembersForUser(Long userId);

	List<UserToMemberMapping> getAllUserRolesForMember(Long userId, Long memberId);
}
