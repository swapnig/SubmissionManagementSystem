package edu.neu.ccis.sms.dao.categories;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.dao.users.UserDao;
import edu.neu.ccis.sms.dao.users.UserDaoImpl;
import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.MemberStatusType;
import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;
import edu.neu.ccis.sms.util.CMISConnector;
import edu.neu.ccis.sms.util.HibernateUtil;

/**
 * DAO Implementation class for Member Entity bean
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @modifiedBy Swapnil Gupta
 * @lastUpdate 7-June-2015
 */
public class MemberDaoImpl implements MemberDao {
    /** Hibernate session instance */
    private Session currentSession;

    /** Hibernate session transaction instance */
    private Transaction currentTransaction;

    /** Default Constructor */
    public MemberDaoImpl() {
    }

    /**
     * private utility book-keeping method to open hibernate session with a new transaction
     * 
     * @return - Hibernate session instance
     */
    private Session openCurrentSessionwithTransaction() {
        currentSession = HibernateUtil.getSessionFactory().openSession();
        currentTransaction = currentSession.beginTransaction();
        return currentSession;
    }

    /**
     * private utility book-keeping method to close current transaction along with hibernate session, committing any new
     * changes to entities; nulling out old references;
     */
    private void closeCurrentSessionwithTransaction() {
        currentTransaction.commit();
        currentSession.close();
        currentTransaction = null;
        currentSession = null;
    }

    /**
     * Getter method for current active hibernate session, if there isn't any active session then returns null
     * 
     * @return - current active hibernate session instance else null
     */
    public Session getCurrentSession() {
        return currentSession;
    }

    /**
     * Getter method for current active hibernate transaction, if there isn't any active transaction then returns null
     * 
     * @return - current active hibernate transaction instance else null
     */
    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    /**
     * Save a new member; This method does not add any default conductor role for a newly created member
     * 
     * @param newMember
     *            - new member to be saved
     */
    @Override
    public void saveMember(final Member newMember) {
        openCurrentSessionwithTransaction();
        // Create the CMS specific folder for this Member
        createCMSFolderForMember(newMember);
        getCurrentSession().save(newMember);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Save a new member; adds a default conductor role for newly created member for given user
     * 
     * @param newMember
     *            - new member to be saved
     * @param conductor
     *            - a user who will assume a default conductor role for this newly created member
     */
    @Override
    public void saveMember(final Member newMember, final User conductor) {
        openCurrentSessionwithTransaction();
        // Create the CMS specific folder for this Member
        createCMSFolderForMember(newMember);
        Long memberId = (Long) getCurrentSession().save(newMember);
        closeCurrentSessionwithTransaction();
        UserDao userDao = new UserDaoImpl();
        userDao.registerUserForMember(conductor.getId(), memberId, RoleType.CONDUCTOR);
    }

    /**
     * saveMember - overloaded method; Assigns default conductor role for user with given userId for a newly created
     * member
     * 
     * @param newMember
     *            - new member to be saved
     * @param conductorUserId
     *            - id of a user who will assume conductor role for newly created member
     */
    @Override
    public void saveMember(final Member newMember, final Long conductorUserId) {
        openCurrentSessionwithTransaction();
        // Create the CMS specific folder for this Member
        createCMSFolderForMember(newMember);
        Long memberId = (Long) getCurrentSession().save(newMember);
        closeCurrentSessionwithTransaction();
        UserDao userDao = new UserDaoImpl();
        userDao.registerUserForMember(conductorUserId, memberId, RoleType.CONDUCTOR);
    }

    /**
     * To create a folder with same member-name in respective folder hierarchy in CMS using CMIS apis
     * 
     * @param member
     *            - member instance for which we are creating the folder in CMS
     */
    private void createCMSFolderForMember(final Member member) {
        // Check if member has no parent,
        if (member.getParentMember() == null) {
            Folder cmsMemberFolder = CMISConnector.createFolderUnderRoot(member.getName());
            member.setCmsFolderId(cmsMemberFolder.getId());
            member.setCmsFolderPath(cmsMemberFolder.getPath());
        } else {
            // If parent member exists, then create a child folder under that parent's folder in CMS
            Folder cmsMemberFolder = CMISConnector.createFolder(member.getParentMember().getCmsFolderPath(),
                    member.getName());
            member.setCmsFolderId(cmsMemberFolder.getId());
            member.setCmsFolderPath(cmsMemberFolder.getPath());
        }
    }

    /**
     * When member name is changed, change the CMS specific folder's name as well and get the updated path and save it
     * into SMS db
     * 
     * @param member
     *            - modified Member instance for which we are updating the CMS folder details
     * @throws Exception
     *             - throws exception if it fails to rename the CMS folder with new member-name
     */
    private void renameCMSFolderForMember(final Member member) throws Exception {
        // first load the Folder details of this member
        Folder cmsMemberFolder = CMISConnector.getFolderById(member.getCmsFolderId());
        String oldName = cmsMemberFolder.getName();
        // Check if the member's name value is changed, if yes then change the CMS folder name as well
        if (!member.getName().equals(oldName)) {
            cmsMemberFolder = CMISConnector.renameFolder(cmsMemberFolder.getId(), member.getName());
        }
        // Changing the name of CMS folder will change the CMS folder path,
        // So update it in SMS database, by setting it in hibernate entity object
        member.setCmsFolderPath(cmsMemberFolder.getPath());
    }

    /**
     * Update member details
     * 
     * @param modifiedMember
     *            - modified member to be saved
     * @throws Exception
     *             - throws exception if it fails to modify related folder in CMS while saving the modified member
     */
    @Override
    public void updateMember(final Member modifiedMember) throws Exception {
        openCurrentSessionwithTransaction();
        renameCMSFolderForMember(modifiedMember);
        getCurrentSession().update(modifiedMember);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Find Member by its member id
     * 
     * @param id
     *            - member id to be retrieved from persistent store
     * @return - Member object if there is a member with given member-id else returns null
     */
    public Member findByMemberId(final Long id) {
        openCurrentSessionwithTransaction();
        Member member = (Member) getCurrentSession().get(Member.class, id);
        closeCurrentSessionwithTransaction();
        return member;
    }

    /**
     * Delete a given member
     * 
     * @param member
     *            - a member to be deleted
     */
    @Override
    public void deleteMember(final Member user) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(user);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Get all available members in current installation
     * 
     * @return List<Member> - list of members retrieved
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Member> getAllMembers() {
        openCurrentSessionwithTransaction();
        List<Member> users = getCurrentSession().createQuery("from Member").list();
        closeCurrentSessionwithTransaction();
        return users;
    }

    /**
     * Get Member by its member id
     * 
     * @param id
     *            - member id to be retrieved from persistent store
     * @return - Member object if there is a member with given member-id else returns null
     */
    @Override
    public Member getMember(final Long id) {
        return findByMemberId(id);
    }

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
    @Override
    @SuppressWarnings("unchecked")
    public Set<Member> findAllMembersByCategoryAndParentMember(final Long categoryId, final Long parentMemberId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
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

    /**
     * Get all members of a given category type and which are under given parent member in its hierarchy tree and which
     * are currently having MemberStatusType as Active
     * 
     * @param categoryId
     *            - id of a category which is a parent category for members to be retrieved
     * @param parentMemberId
     *            - a parent member whose children and sub-children to be searched for given parent category type
     * @return - set of member which have given category as their parent category and which are under given parent
     *         member
     */
    @Override
    @SuppressWarnings("unchecked")
    public Set<Member> findActiveMembersByCategoryAndParentMember(final Long categoryId, final Long parentMemberId) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession()
                .createQuery(
                        "from Member WHERE category_id = :categoryId AND parent_member_id = :parentMemberId and activation_status = :status");
        query.setParameter("categoryId", categoryId);
        query.setParameter("parentMemberId", parentMemberId);
        query.setParameter("status", MemberStatusType.ACTIVE.ordinal());
        List<Member> members = query.list();
        closeCurrentSessionwithTransaction();
        if (null == members) {
            return null;
        } else {
            return new HashSet<Member>(members);
        }
    }

    /**
     * Get all members which are submittable - including all immediate and distant sub-children in its hierarchy; This
     * will also return current parent member in returned list if it is submittable.
     * 
     * @param parentMemberId
     *            - id of member from which to start tracing member hierarchy tree to retrieve all submittable members
     *            list
     * @return - set of members which are submittable
     */
    @Override
    public Set<Member> findAllSubmittableMembersByParentMemberId(final Long parentMemberId) {
        Set<Member> submittableMembers = new HashSet<Member>();
        openCurrentSessionwithTransaction();
        Member member = (Member) getCurrentSession().get(Member.class, parentMemberId);
        if (member.isSubmittable()) {
            submittableMembers.add(member);
        }
        submittableMembers.addAll(findChildSubmittersRecursively(member));
        closeCurrentSessionwithTransaction();
        return submittableMembers;
    }

    /**
     * Recursively trace the whole hierarchy to get all the submittable member nodes
     * 
     * @param member
     *            - parent member node under which to search for submittable members
     * @return - set of nodes which are submittable in current member hierarchy under given parent member node
     */
    private Set<Member> findChildSubmittersRecursively(final Member member) {
        Set<Member> submittableMembers = new HashSet<Member>();

        Set<Member> childMembers = member.getChildMembers();
        if (childMembers != null && !childMembers.isEmpty()) {
            for (Member child : childMembers) {
                if (child.isSubmittable()) {
                    submittableMembers.add(child);
                }
                submittableMembers.addAll(findChildSubmittersRecursively(child));
            }
        }
        return submittableMembers;
    }

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
     * Load Member for a given member id along with its registered users i.e. UserToMemberMapping
     * 
     * @param id
     *            - id of member to retrieve
     * @return - a Member object along with all registrations for this member
     */
    @Override
    public Member getMemberByIdWithUserMappings(final Long id) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "select m from Member m left join fetch m.userToMemberMappings where m.id = :id");
        query.setParameter("id", id);
        List<Member> members = query.list();
        closeCurrentSessionwithTransaction();
        if (members == null || members.isEmpty()) {
            return null;
        } else {
            return members.get(0);
        }
    }

    /**
     * Create a hibernate query based on if parent member id of current node is null or not
     * 
     * @param parentMemberId
     *            - parent member id for current member node
     * @return - Hibernate Query instance based on if given parent member is null or not; if parent member id is null
     *         then its a root - topmost level member node in member node hierarchy tree
     */
    private Query getQueryForParentMemberId(final Long parentMemberId) {
        Query query;
        if (null == parentMemberId) {
            query = getCurrentSession().createQuery(
                    "from Member WHERE parent_member_id is null AND member_name = :memberName");
        } else {
            query = getCurrentSession().createQuery(
                    "from Member WHERE parent_member_id = :parentMemberId AND member_name = :memberName");
            query.setParameter("parentMemberId", parentMemberId);
        }
        return query;
    }

    /**
     * Load Member for given member id along with its all user submissions
     * 
     * @param id
     *            - id of member to retrieve
     * @return - a Member object along with all document submitted for this member
     */
    @Override
    public Member getMemberByIdWithSubmissions(final Long id) {
        openCurrentSessionwithTransaction();
        Query query = getCurrentSession().createQuery(
                "select m from Member m left join fetch m.submissions where m.id = :id");
        query.setParameter("id", id);
        List<Member> members = query.list();
        closeCurrentSessionwithTransaction();
        if (members == null || members.isEmpty()) {
            return null;
        } else {
            return members.get(0);
        }
    }

    /**
     * Get users List for given MemberId which have registered for this member in Evaluator role
     * 
     * @param id
     *            - id of member for which we are finding list of users who have registered in EVALUATOR role
     * @return - set of users who have registered for this member in EVALUATOR role
     */
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

    /**
     * Get users list for given MemberId which have registered for this member in SUBMITTER role
     * 
     * @param id
     *            - id of member for which we are finding list of users who have registered in SUBMITTER role
     * @return - set of users who have registered for this member in SUBMITTER role
     */
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

    /**
     * Get users List for given MemberId which have registered for this member in Conductor role
     * 
     * @param id
     *            - id of member for which we are finding list of users who have registered in CONDUCTOR role
     * @return - set of users who have registered for this member in CONDUCTOR role
     */
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
    }

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
    @Override
    public void changeChildMemberActivationStatusByParentMemberId(final Long parentMemberId,
            final MemberStatusType status) throws Exception
    {
        openCurrentSessionwithTransaction();
        Member member = (Member) getCurrentSession().get(Member.class, parentMemberId);
        changeChildMemberActivationStatusRecursively(member, status);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Recursively trace the whole hierarchy under given member node and change all its children's status to new given
     * MemberStatusType
     * 
     * @param member
     *            - member node under which all node's status will be changed to new status
     * @param status
     *            - new MemberStatusType status
     * @throws Exception
     *             - throws exception if it fails to change MemberStatusType status of any of its child nodes
     */
    private void changeChildMemberActivationStatusRecursively(final Member member, final MemberStatusType status)
            throws Exception
    {

        Set<Member> childMembers = member.getChildMembers();
        if (childMembers != null && !childMembers.isEmpty()) {
            for (Member child : childMembers) {
                child.setActivationStatus(status);
                getCurrentSession().update(child);
                changeChildMemberActivationStatusRecursively(child, status);
            }
        }
    }
}
