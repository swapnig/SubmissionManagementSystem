package edu.neu.ccis.sms.dao.categories;

import java.util.List;
import java.util.Set;

import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.users.User;

/**
 * DAO interface for Member Entity bean
 * 
 * @author Pramod R. Khare, Swapnil Gupta
 * @LastModifiedBy Pramod Khare
 * @date 9-May-2015
 * @lastUpdate 10-June-2015
 */
public interface MemberDao {
    // Member
    public List<Member> getAllMembers();

    public Member getMember(final Long id);

    public void updateMember(Member modifiedMember) throws Exception;

    public void deleteMember(Member member);

    public void saveMember(Member newMember);

    public void saveMember(Member newMember, User conductor);

    public void saveMember(Member newMember, Long conductorUserId);

    public Set<Member> findAllMembersByCategoryAndParentMember(final Long categoryId, final Long parentMemberId);

    public Set<Member> findAllSubmittableMembersByParentMemberId(final Long parentMemberId);

    public Member getMemberByIdWithUserMappings(final Long id);

    public Member getMemberByIdWithSubmissions(final Long id);

    /* Get submitters list for given MemberId */
    public Set<User> getSubmittersForMemberId(final Long id);

    /* Get Evaluators User List for given MemberId */
    public Set<User> getEvaluatorsForMemberId(final Long id);

    /* Get conductors list for given MemberId */
    public Set<User> getConductorsForMemberId(final Long id);

    public boolean doesMemberNameExistForParentMember(final String memberName, final Long parentMemberId);
}
