package edu.neu.ccis.sms.dao.categories;

import java.util.List;

import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.users.RoleType;

public interface UserToMemberMappingDao {
    /**
     * Get all user to member registrations for all available members;
     * 
     * @return
     */
    public List<UserToMemberMapping> getAllUserToMemberMappings();

    /**
     * Get member registration (User to member mapping) details for a given registration id
     * 
     * @param id
     * @return
     */
    public UserToMemberMapping getUserToMemberMapping(Long id);

    /**
     * Update already existing user to member registration details
     * 
     * @param modifiedUserToMemberMapping
     */
    public void updateUserToMemberMapping(UserToMemberMapping modifiedUserToMemberMapping);

    /**
     * Delete a user to member registration
     * 
     * @param userToMemberMapping
     */
    public void deleteUserToMemberMapping(UserToMemberMapping userToMemberMapping);

    /**
     * Save a new user to member registration
     * 
     * @param newUserToMemberMapping
     */
    public void saveUserToMemberMapping(UserToMemberMapping newUserToMemberMapping);

    /**
     * Get all members to which given user id has registrations with (including all registration statuses)
     * 
     * @param userId
     * @return
     */
    public List<UserToMemberMapping> getAllMembersForUser(Long userId);

    /**
     * Checks if given user has a specific role registration for a given member
     * 
     * @param userId
     * @param role
     * @param memberId
     * @return
     */
    public boolean doesUserHaveRoleForMember(Long userId, RoleType role, Long memberId);

    /**
     * Get user's role for given member registration, if user is not registered with given member it will return null
     * 
     * @param userId
     * @param memberId
     * @return
     */
    public RoleType getUsersRoleForMember(final Long userId, final Long memberId);

    /**
     * Get all registerable members for which given user has registered with
     * 
     * @param userId
     * @return
     */
    public List<UserToMemberMapping> getAllRegisterableMembersForUser(Long userId);

    /**
     * Get all roles a given user has assumed for a given member
     * 
     * @param userId
     * @param memberId
     * @return
     */
    List<UserToMemberMapping> getAllUserRolesForMember(Long userId, Long memberId);
}
