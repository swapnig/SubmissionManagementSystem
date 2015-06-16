package edu.neu.ccis.sms.dao.categories;

import java.util.List;
import java.util.Set;

import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.users.User;

public interface MemberDao {
	// Member
	public List<Member> getAllMembers();

	public Member getMember(Long id);

	public void updateMember(Member modifiedMember);

	public void deleteMember(Member member);

	public void saveMember(Member newMember);

	public void saveMember(Member newMember, User conductor);

	public void saveMember(Member newMember, Long conductorUserId);

	public Set<Member> findAllMembersByCategoryAndParentMember(Long categoryId, Long parentMemberId);

	public Set<Member> findAllSubmittableMembersByParentMemberId(Long parentMemberId);

	public boolean doesMemberNameExistForParentMember(String memberName, Long parentMemberId);
}
