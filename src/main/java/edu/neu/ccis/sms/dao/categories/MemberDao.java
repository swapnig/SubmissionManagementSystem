package edu.neu.ccis.sms.dao.categories;

import java.util.List;
import java.util.Set;

import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberStatusType;
import edu.neu.ccis.sms.entity.users.User;

/**
 * DAO interface for Member Entity bean; Provides access methods for accessing
 * 
 * @author Pramod R. Khare, Swapnil Gupta
 * @LastModifiedBy Pramod Khare
 * @date 9-May-2015
 * @lastUpdate 10-June-2015
 */
public interface MemberDao {
    /**
     * Get all available members in current installation
     * 
     * @return
     */
    public List<Member> getAllMembers();

    /** Get Member by its member id */
    public Member getMember(final Long id);

    /** Update member details */
    public void updateMember(Member modifiedMember) throws Exception;

    /** Delete a given member */
    public void deleteMember(Member member);

    /** Save a new member; This implementation will not ad any default conductor role for a newly created member */
    public void saveMember(Member newMember);

    /** Save a new member; adds a default conductor role for newly created member */
    public void saveMember(Member newMember, User conductor);

    /** saveMember - overloaded method; Takes userId as a default conductor for a newly created member */
    public void saveMember(Member newMember, Long conductorUserId);

    /** Get all members of a given category type and which are under given parent member in its hierarchy tree */
    public Set<Member> findAllMembersByCategoryAndParentMember(final Long categoryId, final Long parentMemberId);

    /**
     * Get all members which are submittable - including all immediate and distant sub-children in its hierarchy; This
     * will also return current parent member in returned list if it is submittable.
     * 
     * @param parentMemberId
     * @return
     */
    public Set<Member> findAllSubmittableMembersByParentMemberId(final Long parentMemberId);

    /**
     * Load Member for given member id along with its registered users
     * 
     * @param id
     * @return
     */
    public Member getMemberByIdWithUserMappings(final Long id);

    /**
     * Load Member for given member id along with its all user submissions
     * 
     * @param id
     * @return
     */
    public Member getMemberByIdWithSubmissions(final Long id);

    /**
     * Get users list for given MemberId which have registered for this member in SUBMITTER role
     * 
     * @param id
     * @return
     */
    public Set<User> getSubmittersForMemberId(final Long id);

    /**
     * Get users List for given MemberId which have registered for this member in Evaluator role
     * 
     * @param id
     * @return
     */
    public Set<User> getEvaluatorsForMemberId(final Long id);

    /**
     * Get users List for given MemberId which have registered for this member in Conductor role
     * 
     * @param id
     * @return
     */
    public Set<User> getConductorsForMemberId(final Long id);

    /**
     * Checks if for a given member are there any of its child members have a given membername
     * 
     * @param memberName
     * @param parentMemberId
     * @return
     */
    public boolean doesMemberNameExistForParentMember(final String memberName, final Long parentMemberId);

    /**
     * Change Activation status for all children, sub-children members with new status for given parent member
     * 
     * @param parentMemberId
     * @param status
     * @throws Exception
     */
    public void changeChildMemberActivationStatusByParentMemberId(Long parentMemberId, MemberStatusType status)
            throws Exception;

    /**
     * Get all members of a given category type and which are under given parent member in its hierarchy tree and which
     * are currntly having MemberStatusType as Active
     * 
     * @param categoryId
     * @param parentMemberId
     * @return
     */
    public Set<Member> findActiveMembersByCategoryAndParentMember(Long categoryId, Long parentMemberId);
}
