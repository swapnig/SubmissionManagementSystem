package edu.neu.ccis.sms.dao.categories;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;
import edu.neu.ccis.sms.util.HibernateUtil;

public class MemberDaoImpl implements MemberDao {
	private Session currentSession;
	private Transaction currentTransaction;

	public MemberDaoImpl() {
	}

	public Session openCurrentSessionwithTransaction() {
		currentSession = getSessionFactory().openSession();
		currentTransaction = currentSession.beginTransaction();
		return currentSession;
	}

	public void closeCurrentSessionwithTransaction() {
		currentTransaction.commit();
		currentSession.close();
	}

	private static SessionFactory getSessionFactory() {
		return HibernateUtil.getSessionFactory();
	}

	public Session getCurrentSession() {
		return currentSession;
	}

	public void setCurrentSession(final Session currentSession) {
		this.currentSession = currentSession;
	}

	public Transaction getCurrentTransaction() {
		return currentTransaction;
	}

	public void setCurrentTransaction(final Transaction currentTransaction) {
		this.currentTransaction = currentTransaction;
	}

	@Override
	public void saveMember(final Member newMember) {
		openCurrentSessionwithTransaction();
		getCurrentSession().save(newMember);
		closeCurrentSessionwithTransaction();
	}

	@Override
	public void saveMember(final Member newMember, final User conductor) {
		openCurrentSessionwithTransaction();
		Long memberId = (Long) getCurrentSession().save(newMember);
		closeCurrentSessionwithTransaction();
		UserDao userDao = new UserDaoImpl();
		userDao.registerUserForMember(conductor.getId(), memberId,
				RoleType.CONDUCTOR);
	}

	@Override
	public void saveMember(final Member newMember, final Long conductorUserId) {
		openCurrentSessionwithTransaction();
		Long memberId = (Long) getCurrentSession().save(newMember);
		closeCurrentSessionwithTransaction();
		UserDao userDao = new UserDaoImpl();
		userDao.registerUserForMember(conductorUserId, memberId,
				RoleType.CONDUCTOR);
	}

	@Override
	public void updateMember(final Member modifiedMember) {
		openCurrentSessionwithTransaction();
		getCurrentSession().update(modifiedMember);
		closeCurrentSessionwithTransaction();
	}

	public Member findByMemberId(final Long id) {
		openCurrentSessionwithTransaction();
		Member member = (Member) getCurrentSession().get(Member.class, id);
		closeCurrentSessionwithTransaction();
		return member;
	}

	@Override
	public void deleteMember(final Member user) {
		openCurrentSessionwithTransaction();
		getCurrentSession().delete(user);
		closeCurrentSessionwithTransaction();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Member> getAllMembers() {
		openCurrentSessionwithTransaction();
		List<Member> users = getCurrentSession().createQuery(
				"from Member").list();
		closeCurrentSessionwithTransaction();
		return users;
	}

	public void deleteAll() {
		List<Member> usersList = getAllMembers();
		for (Member user : usersList) {
			deleteMember(user);
		}
	}

	@Override
	public Member getMember(final Long id) {
		return findByMemberId(id);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Set<Member> findAllMembersByCategoryAndParentMember(final Long categoryId,
			final Long parentMemberId) {
		openCurrentSessionwithTransaction();
		Query query = getCurrentSession()
				.createQuery(
						"from Member WHERE category_id = :categoryId AND parent_member_id = :parentMemberId");
		query.setParameter("categoryId", categoryId);
		query.setParameter("parentMemberId", parentMemberId);
		List<Member> members = query.list();
		closeCurrentSessionwithTransaction();
		if (null == members) {
			return null;
		} else {
			return new HashSet<Member>(members);
		}

	}

	@Override
	public Set<Member> findAllSubmittableMembersByParentMemberId(
			final Long parentMemberId) {
		Set<Member> submittableMembers = new HashSet<Member>();
		openCurrentSessionwithTransaction();
		Member member = (Member) getCurrentSession().get(Member.class,
				parentMemberId);
		if (member.isSubmittable()) {
			submittableMembers.add(member);
		}
		submittableMembers.addAll(findChildSubmittersRecursively(member));
		closeCurrentSessionwithTransaction();
		return submittableMembers;
	}

	/**
	 * Recursively trace the whole hierarchy to get all the submittable member
	 * nodes
	 * 
	 * @param member
	 * @return
	 */
	private Set<Member> findChildSubmittersRecursively(final Member member) {
		Set<Member> submittableMembers = new HashSet<Member>();

		Set<Member> childMembers = member.getChildMembers();
		if (childMembers != null && !childMembers.isEmpty()) {
			for (Member child : childMembers) {
				if (child.isSubmittable()) {
					submittableMembers.add(child);
				}
				submittableMembers
				.addAll(findChildSubmittersRecursively(child));
			}
		}
		return submittableMembers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean doesMemberNameExistForParentMember(final String memberName, final Long parentMemberId) {
		openCurrentSessionwithTransaction();
		Query query = getQueryForParentMemberId(parentMemberId);
		query.setParameter("memberName", memberName);

		List<Member> members = query.list();
		closeCurrentSessionwithTransaction();
		if (CollectionUtils.isEmpty(members)) {
			return false;
		} else {
			return true;
		}
	}

	private Query getQueryForParentMemberId(final Long parentMemberId) {
		Query query;
		if(null == parentMemberId) {
			query = getCurrentSession()
					.createQuery("from Member WHERE parent_member_id is null AND member_name = :memberName");
		} else {
			query = getCurrentSession()
					.createQuery("from Member WHERE parent_member_id = :parentMemberId AND member_name = :memberName");
			query.setParameter("parentMemberId", parentMemberId);
		}
		return query;
	}
}
