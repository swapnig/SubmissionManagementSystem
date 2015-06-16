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
import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;
import edu.neu.ccis.sms.util.HibernateUtil;

/**
 * DAO Implementation class for Member Entity bean
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
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
		List<Member> users = (List<Member>) getCurrentSession().createQuery(
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
	
	/**
     * Get Member by memberId along with UserToMemberMappings
     */
    @Override
    public Member getMemberByIdWithUserMappings(final Long id) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "select m from Member m left join fetch m.userToMemberMappings where m.id = :id");
        query.setParameter("id", id);
        List<Member> members = (List<Member>) query.list();
        closeCurrentSessionwithTransaction();
        if (members == null || members.isEmpty()) {
            return null;
        } else {
            return members.get(0);
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

	/**
     * Get Member by memberId along with UserToMemberMappings
     */
    @Override
    public Member getMemberByIdWithSubmissions(final Long id) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "select m from Member m left join fetch m.submissions where m.id = :id");
        query.setParameter("id", id);
        List<Member> members = (List<Member>) query.list();
        closeCurrentSessionwithTransaction();
        if (members == null || members.isEmpty()) {
            return null;
        } else {
            return members.get(0);
        }
    }

    @Override
    public Set<User> getEvaluatorsForMemberId(final Long id) {
        final Member member = getMemberByIdWithUserMappings(id);
        final Set<User> evaluators = new HashSet<User>();
        for (UserToMemberMapping mapping : member.getUserToMemberMappings()) {
            if (mapping.getRole() == RoleType.EVALUATOR) {
                evaluators.add(mapping.getUser());
            }
        }
        return evaluators;
    }

    @Override
    public Set<User> getSubmittersForMemberId(final Long id) {
        final Member member = getMemberByIdWithUserMappings(id);
        final Set<User> submitters = new HashSet<User>();
        for (UserToMemberMapping mapping : member.getUserToMemberMappings()) {
            if (mapping.getRole() == RoleType.SUBMITTER) {
                submitters.add(mapping.getUser());
            }
        }
        return submitters;
    }

    @Override
    public Set<User> getConductorsForMemberId(final Long id) {
        final Member member = getMemberByIdWithUserMappings(id);
        final Set<User> conductors = new HashSet<User>();
        for (UserToMemberMapping mapping : member.getUserToMemberMappings()) {
            if (mapping.getRole() == RoleType.CONDUCTOR) {
                conductors.add(mapping.getUser());
            }
        }
        return conductors;
    }}
