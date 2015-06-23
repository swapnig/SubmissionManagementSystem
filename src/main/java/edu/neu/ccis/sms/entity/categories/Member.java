package edu.neu.ccis.sms.entity.categories;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.neu.ccis.sms.entity.submissions.Document;
import edu.neu.ccis.sms.entity.submissions.EvalType;

/**
 * Hibernate Entity bean class for Member; Members are simply instances of a Category
 * 
 * @author Pramod R. Khare
 * @modifedBy Swapnil Gupta
 * @date 9-May-2015
 * @lastUpdate 10-June-2015
 */
@Entity
@Table(name = "Member", uniqueConstraints = { @UniqueConstraint(columnNames = "MEMBER_ID") })
public class Member implements Serializable, Comparable<Member> {
    private static final long serialVersionUID = -2408178701425545266L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ID", unique = true, nullable = false)
    private Long id;

    /** member name */
    @Column(name = "MEMBER_NAME", nullable = false)
    private String name;

    /**
     * CMS folder id - For every member we create a corresponding CMS folder with same name - so we replicate the Member
     * hierarchy into CMS via folder hierarchy
     */
    @Column(name = "CMS_FOLDER_ID")
    private String cmsFolderId;

    /** CMS folder path */
    @Column(name = "CMS_FOLDER_PATH")
    private String cmsFolderPath;

    /**
     * boolean flag indicating if this member is registerable member i.e. to which users can register to
     */
    @Column(name = "IS_REGISTERABLE", nullable = false)
    private boolean isRegisterable = false;

    /**
     * boolean flag indicating if this member is submittable members i.e. to which users can submit their assignments to
     */
    @Column(name = "IS_SUBMITTABLE", nullable = false)
    private boolean isSubmittable = false;

    /** Category from which this member is created from */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    /** parent member of this member in the member hierarchy tree */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_MEMBER_ID")
    private Member parentMember;

    /** list of all children members of this member in the member hierarchy tree */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentMember", cascade = CascadeType.ALL)
    private Set<Member> childMembers = new HashSet<Member>();

    /** List of attributes of this member */
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL, CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "member")
    @Column(nullable = false)
    private Set<MemberAttribute> attributes = new HashSet<MemberAttribute>();

    /**
     * List of posts for this forum member - kept for future reference - when a Discussion Forum member functionality is
     * implemented
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "memberCategory")
    @Column(nullable = true)
    private Set<Post> posts = new HashSet<Post>();

    /**
     * User to Member registration mapping - with their other attributes like registration dates, their role, the
     * registration status, etc.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "member")
    @Column(nullable = false)
    private Set<UserToMemberMapping> userToMemberMappings = new HashSet<UserToMemberMapping>();

    /*************************************************************************
     * Following attributes/columns are related only to submittable members
     *************************************************************************/

    /**
     * This flag keeps track if final evaluations are calculated for each individual submitted document from their
     * possibly multiple evaluations.
     * 
     * This flag will be made true when Coductor clicks on "disseminating grades" to students // or while calculating
     * fairness of grades - currently its done during "disseminating grades" to students.
     */
    @Column(name = "IS_FINAL_EVALUATED", nullable = false)
    private boolean isFinalEvaluated = false;

    /**
     * The default EvalType - for final Evaluation calculation for submitted documents;
     * 
     * This can be changed while - a) Disseminating grades to students or b) while calculating fairness of grades IMP -
     * 
     * Currently it is done - while Disseminating grades to students, as fairness calculation is a future work of this
     * project
     */
    @Column(name = "EVAL_TYPE", nullable = false, updatable = true)
    private EvalType finalEvalType = EvalType.AVERAGE;

    /** Member activation status - if this member is active or inactivated */
    @Column(name = "ACTIVATION_STATUS", nullable = false, updatable = true)
    private MemberStatusType activationStatus = MemberStatusType.ACTIVE;

    /** All submitted documents for this submittable member */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "submittedForMember")
    @Column(nullable = true)
    private Set<Document> submissions = new HashSet<Document>();

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public EvalType getFinalEvalType() {
        return finalEvalType;
    }

    public void setFinalEvalType(final EvalType finalEvalType) {
        this.finalEvalType = finalEvalType;
    }

    public MemberStatusType getActivationStatus() {
        return activationStatus;
    }

    public void setActivationStatus(final MemberStatusType activationStatus) {
        this.activationStatus = activationStatus;
    }

    public boolean isFinalEvaluated() {
        return isFinalEvaluated;
    }

    public void setFinalEvaluated(final boolean isFinalEvaluated) {
        this.isFinalEvaluated = isFinalEvaluated;
    }

    public Set<Document> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(final Set<Document> submissions) {
        this.submissions = submissions;
    }

    public boolean addSubmissions(final Document submission) {
        return submissions.add(submission);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCmsFolderId() {
        return cmsFolderId;
    }

    public void setCmsFolderId(final String cmsFolderId) {
        this.cmsFolderId = cmsFolderId;
    }

    public String getCmsFolderPath() {
        return cmsFolderPath;
    }

    public void setCmsFolderPath(final String cmsFolderPath) {
        this.cmsFolderPath = cmsFolderPath;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(final Category category) {
        isRegisterable = category.isRegisterable();
        isSubmittable = category.isSubmittable();
        this.category = category;
    }

    public boolean isRegisterable() {
        return isRegisterable;
    }

    public void setRegisterable(final boolean isRegisterable) {
        this.isRegisterable = isRegisterable;
    }

    public boolean isSubmittable() {
        return isSubmittable;
    }

    public void setSubmittable(final boolean isSubmittable) {
        this.isSubmittable = isSubmittable;
    }

    public Member getParentMember() {
        return parentMember;
    }

    public void setParentMember(final Member parentMember) {
        this.parentMember = parentMember;
    }

    public Set<Member> getChildMembers() {
        return childMembers;
    }

    public void setChildMembers(final Set<Member> childMembers) {
        this.childMembers = childMembers;
    }

    public boolean addChildMembers(final Member childMember) {
        return childMembers.add(childMember);
    }

    public Set<MemberAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(final Set<MemberAttribute> attributes) {
        this.attributes = attributes;
    }

    public boolean addAttributes(final MemberAttribute attribute) {
        return attributes.add(attribute);
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(final Set<Post> posts) {
        this.posts = posts;
    }

    public boolean addPost(final Post post) {
        return posts.add(post);
    }

    public Set<UserToMemberMapping> getUserToMemberMappings() {
        return userToMemberMappings;
    }

    public void setUserToMemberMappings(final Set<UserToMemberMapping> userToMemberMappings) {
        this.userToMemberMappings = userToMemberMappings;
    }

    public boolean addUserToMemberMapping(final UserToMemberMapping userToMemberMapping) {
        return userToMemberMappings.add(userToMemberMapping);
    }

    @Override
    public int compareTo(final Member o) {
        return id.compareTo(o.getId());
    }

    @Override
    public boolean equals(final Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Member) {
            Member member = (Member) anObject;
            return id.equals(member.id);
        }
        return false;
    }
}
