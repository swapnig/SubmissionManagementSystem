package edu.neu.ccis.sms.dao.categories;

import java.util.List;
import java.util.Set;

import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberStatusType;
import edu.neu.ccis.sms.entity.users.User;

/**
 * DAO interface for Member Entity bean; Provides access methods for accessing members from persistent store
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @modifiedBy Swapnil Gupta
 * @lastUpdate 10-June-2015
 */
public interface MemberDao {
    /**
     * Get all available members in current installation
     * 
     * @return List<Member> - list of members retrieved
     */
    public List<Member> getAllMembers();

    /**
     * Get Member by its member id
     * 
     * @param id
     *            - member id to be retrieved from persistent store
     * @return - Member object if there is a member with given member-id else returns null
     */
    public Member getMember(final Long id);

    /**
     * Update member details
     * 
     * @param modifiedMember
     *            - modified member to be saved
     * @throws Exception
     *             - throws exception if it fails to modify related folder in CMS while saving the modified member
     */
    public void updateMember(Member modifiedMember) throws Exception;

    /**
     * Delete a given member
     * 
     * @param member
     *            - a member to be deleted
     */
    public void deleteMember(Member member);

    /**
     * Save a new member; This method does not add any default conductor role for a newly created member
     * 
     * @param newMember
     *            - new member to be saved
     */
    public void saveMember(Member newMember);

    /**
     * Save a new member; adds a default conductor role for newly created member for given user
     * 
     * @param newMember
     *            - new member to be saved
     * @param conductor
     *            - a user who will assume a default conductor role for this newly created member
     */
    public void saveMember(Member newMember, User conductor);

    /**
     * saveMember - overloaded method; Assigns default conductor role for user with given userId for a newly created
     * member
     * 
     * @param newMember
     *            - new member to be saved
     * @param conductorUserId
     *            - id of a user who will assume conductor role for newly created member
     */
    public void saveMember(Member newMember, Long conductorUserId);

    /**
     * Get all members of a given category type (given by category-id) and which are under given parent member (given by
     * parent member id) in its member hierarchy tree
     * 
     * @param categoryId
     *            - id of a category who is parent category
     * @param parentMemberId
     *            - id of a parent member in the member hierarchy tree
     * @return - set of members who are of given parent category type and are under given parent member in member
     *         hierarchy tree
     */
    public Set<Member> findAllMembersByCategoryAndParentMember(final Long categoryId, final Long parentMemberId);

    /**
     * Get all members which are submittable - including all immediate and distant sub-children in its hierarchy; This
     * will also return current parent member in returned list if it is submittable.
     * 
     * @param parentMemberId
     *            - id of member from which to start tracing member hierarchy tree to retrieve all submittable members
     *            list
     * @return - set of members which are submittable
     */
    public Set<Member> findAllSubmittableMembersByParentMemberId(final Long parentMemberId);

    /**
     * Load Member for a given member id along with its registered users i.e. UserToMemberMapping
     * 
     * @param id
     *            - id of member to retrieve
     * @return - a Member object along with all registrations for this member
     */
    public Member getMemberByIdWithUserMappings(final Long id);

    /**
     * Load Member for given member id along with its all user submissions
     * 
     * @param id
     *            - id of member to retrieve
     * @return - a Member object along with all document submitted for this member
     */
    public Member getMemberByIdWithSubmissions(final Long id);

    /**
     * Get users list for given MemberId which have registered for this member in SUBMITTER role
     * 
     * @param id
     *            - id of member to retrieve
     * @return - set of users who have registered for this member in SUBMITTER role
     */
    public Set<User> getSubmittersForMemberId(final Long id);

    /**
     * Get users List for given MemberId which have registered for this member in Evaluator role
     * 
     * @param id
     *            - id of member to retrieve
     * @return - set of users who have registered for this member in EVALUATOR role
     */
    public Set<User> getEvaluatorsForMemberId(final Long id);

    /**
     * Get users List for given MemberId which have registered for this member in Conductor role
     * 
     * @param id
     *            - id of member to retrieve
     * @return - set of users who have registered for this member in CONDUCTOR role
     */
    public Set<User> getConductorsForMemberId(final Long id);

    /**
     * Checks if for a given member are there any of its child members have a given membername
     * 
     * @param memberName
     *            - member name
     * @param parentMemberId
     *            - id of parent member under which to search if any of its chidren, sub-children have a given member
     *            name
     * @return - boolean result - if there is a member in the member hierarchy under given parent member with given name
     */
    public boolean doesMemberNameExistForParentMember(final String memberName, final Long parentMemberId);

    /**
     * Change Activation status for all children, sub-children members under given parent member with new status
     * 
     * @param parentMemberId
     *            - a parent member id under which all its children and sub-children will be changed their status
     * @param status
     *            - MemberStatusType for all sub-children and children under given parent member
     * @throws Exception
     *             - throws exception if it fails to change status of all its children and sub-children for given parent
     *             member
     */
    public void changeChildMemberActivationStatusByParentMemberId(Long parentMemberId, MemberStatusType status)
            throws Exception;

    /**
     * Get all members of a given category type and which are under given parent member in its hierarchy tree and which
     * are currntly having MemberStatusType as Active
     * 
     * @param categoryId
     *            - id of a category which is a parent category for members to be retrieved
     * @param parentMemberId
     *            - a parent member whose children and sub-children to be searched for given parent category type
     * @return - set of member which have given category as their parent category and which are under given parent
     *         member
     */
    public Set<Member> findActiveMembersByCategoryAndParentMember(Long categoryId, Long parentMemberId);
}
