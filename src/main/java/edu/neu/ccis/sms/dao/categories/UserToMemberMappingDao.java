package edu.neu.ccis.sms.dao.categories;

import java.util.List;

import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.users.RoleType;

/**
 * DAO interface for all user to member registration mapping entity accesses
 * 
 * @author Pramod R Khare, Swapnil Gupta
 * @date 9-May-2015
 */
public interface UserToMemberMappingDao {
    /**
     * Get all user to member registrations for all available members;
     * 
     * @return - list of all user-to-member registration mappings
     */
    public List<UserToMemberMapping> getAllUserToMemberMappings();

    /**
     * Get member registration (User to member mapping) details for a given registration id
     * 
     * @param id
     *            - registration mapping id
     * @return - User to member registration mapping for given registration mapping id
     */
    public UserToMemberMapping getUserToMemberMapping(Long id);

    /**
     * Update already existing user to member registration details mapping
     * 
     * @param modifiedUserToMemberMapping
     *            - user to member registration mapping to be modified
     */
    public void updateUserToMemberMapping(UserToMemberMapping modifiedUserToMemberMapping);

    /**
     * Delete a user to member registration mapping
     * 
     * @param userToMemberMapping
     *            - user to member registration object to be deleted
     */
    public void deleteUserToMemberMapping(UserToMemberMapping userToMemberMapping);

    /**
     * Save a new user to member registration
     * 
     * @param newUserToMemberMapping
     *            - new registration details object
     */
    public void saveUserToMemberMapping(UserToMemberMapping newUserToMemberMapping);

    /**
     * Get all members to which given user id has registrations with (including all registration statuses, in all
     * registration role types)
     * 
     * @param userId
     *            - user id
     * @return - list of user-to-member registrations for a given user
     */
    public List<UserToMemberMapping> getAllMembersForUser(Long userId);

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
    public boolean doesUserHaveRoleForMember(Long userId, RoleType role, Long memberId);

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
    public RoleType getUsersRoleForMember(final Long userId, final Long memberId);

    /**
     * Get all registerable members for which given user has registered with
     * 
     * @param userId
     *            - id of user
     * @return - list of mappings for which given user has registered with some kind of role
     */
    public List<UserToMemberMapping> getAllRegisterableMembersForUser(Long userId);

    /**
     * Get all roles a given user has assumed for a given member
     * 
     * @param userId
     *            - id of user
     * @param memberId
     *            - id of member
     * @return - list of role mappings that given user have for given member
     */
    List<UserToMemberMapping> getAllUserRolesForMember(Long userId, Long memberId);
}
