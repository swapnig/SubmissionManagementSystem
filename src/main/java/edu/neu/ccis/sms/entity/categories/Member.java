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
 * Hibernate Entity bean class for Member; Members are simply instances of a
 * Category
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

    @Column(name = "MEMBER_NAME", nullable = false)
    private String name;

    @Column(name = "CMS_FOLDER_ID")
    private String cmsFolderId;

    @Column(name = "CMS_FOLDER_PATH")
    private String cmsFolderPath;

    @Column(name = "IS_REGISTERABLE", nullable = false)
    private boolean isRegisterable = false;

    @Column(name = "IS_SUBMITTABLE", nullable = false)
    private boolean isSubmittable = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_MEMBER_ID")
    private Member parentMember;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parentMember", cascade = CascadeType.ALL)
    private Set<Member> childMembers = new HashSet<Member>();

    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL, CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "member")
    @Column(nullable = false)
    private Set<MemberAttribute> attributes = new HashSet<MemberAttribute>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "memberCategory")
    @Column(nullable = true)
    private Set<Post> posts = new HashSet<Post>();

    /**
     * User to Member registration mapping - with their other attributes like
     * registration dates, their role, the registration status, etc.
     */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "member")
    @Column(nullable = false)
    private Set<UserToMemberMapping> userToMemberMappings = new HashSet<UserToMemberMapping>();

    /*************************************************************************
     * Following attributes/columns are related only to submittable members
     *************************************************************************/

    /**
     * This flag keeps track if final evaluations are calculated for each
     * individual submitted document from their possibly multiple evaluations.
     * 
     * This flag will be made true when Coductor clicks on
     * "disseminating grades" to students // or while calculating fairness of
     * grades - currently its done during "disseminating grades" to students.
     */
    @Column(name = "IS_FINAL_EVALUATED", nullable = false)
    private boolean isFinalEvaluated = false;

    // The default EvalType - for final Evaluation calculation for submitted
    // documents, This can be changed while - Disseminating grades to students
    // or while calculating fairness of grades
    // IMP - Currently it is done - while Disseminating grades to students, as
    // fairness calculation is a future work of this project
    @Column(name = "EVAL_TYPE", nullable = false, updatable = true)
    private EvalType finalEvalType = EvalType.AVERAGE;

    /**
     * All submitted documents for this submittable member
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "submittedForMember")
    @Column(nullable = true)
    private Set<Document> submissions = new HashSet<Document>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EvalType getFinalEvalType() {
        return finalEvalType;
    }

    public void setFinalEvalType(EvalType finalEvalType) {
        this.finalEvalType = finalEvalType;
    }

    public boolean isFinalEvaluated() {
        return isFinalEvaluated;
    }

    public void setFinalEvaluated(boolean isFinalEvaluated) {
        this.isFinalEvaluated = isFinalEvaluated;
    }

    public Set<Document> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Set<Document> submissions) {
        this.submissions = submissions;
    }

    public boolean addSubmissions(Document submission) {
        return this.submissions.add(submission);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCmsFolderId() {
        return cmsFolderId;
    }

    public void setCmsFolderId(String cmsFolderId) {
        this.cmsFolderId = cmsFolderId;
    }

    public String getCmsFolderPath() {
        return cmsFolderPath;
    }

    public void setCmsFolderPath(String cmsFolderPath) {
        this.cmsFolderPath = cmsFolderPath;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.isRegisterable = category.isRegisterable();
        this.isSubmittable = category.isSubmittable();
        this.category = category;
    }

    public boolean isRegisterable() {
        return isRegisterable;
    }

    public void setRegisterable(boolean isRegisterable) {
        this.isRegisterable = isRegisterable;
    }

    public boolean isSubmittable() {
        return isSubmittable;
    }

    public void setSubmittable(boolean isSubmittable) {
        this.isSubmittable = isSubmittable;
    }

    public Member getParentMember() {
        return parentMember;
    }

    public void setParentMember(Member parentMember) {
        this.parentMember = parentMember;
    }

    public Set<Member> getChildMembers() {
        return childMembers;
    }

    public void setChildMembers(Set<Member> childMembers) {
        this.childMembers = childMembers;
    }

    public boolean addChildMembers(Member childMember) {
        return this.childMembers.add(childMember);
    }

    public Set<MemberAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<MemberAttribute> attributes) {
        this.attributes = attributes;
    }

    public boolean addAttributes(MemberAttribute attribute) {
        return this.attributes.add(attribute);
    }

    public Set<Post> getPosts() {
        return posts;
    }

    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }

    public boolean addPost(Post post) {
        return this.posts.add(post);
    }

    public Set<UserToMemberMapping> getUserToMemberMappings() {
        return userToMemberMappings;
    }

    public void setUserToMemberMappings(Set<UserToMemberMapping> userToMemberMappings) {
        this.userToMemberMappings = userToMemberMappings;
    }

    public boolean addUserToMemberMapping(UserToMemberMapping userToMemberMapping) {
        return this.userToMemberMappings.add(userToMemberMapping);
    }

    @Override
    public int compareTo(Member o) {
        return this.id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Member) {
            Member member = (Member) anObject;
            return (this.id.equals(member.id));
        }
        return false;
    }
}
