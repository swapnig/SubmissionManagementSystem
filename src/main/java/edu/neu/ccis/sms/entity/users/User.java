package edu.neu.ccis.sms.entity.users;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.submissions.Document;

/**
 * User Hibernate Entity bean class; contains all important information about
 * user, his submissions and his member-registration mappings, and other
 * personal information
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 10-June-2015
 */
@Entity
@Table(name = "User", uniqueConstraints = { @UniqueConstraint(columnNames = "USER_ID"),
        @UniqueConstraint(columnNames = "EMAIL"), @UniqueConstraint(columnNames = "USERNAME") })
public class User implements Serializable, Comparable<User> {
    private static final long serialVersionUID = -4572727294954027970L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "FIRSTNAME", length = 255, nullable = false)
    private String firstname;

    @Column(name = "LASTNAME", length = 255)
    private String lastname;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    // TODO - Use MD5 hashing
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "USERNAME", nullable = false, length = 100)
    private String username;

    /** Optional activation code. */
    @Column(name = "ACTIVATION", length = 100)
    private String activation;

    /** Optional expiration time of the activation code. */
    @Column(name = "EXPIRATION", length = 100)
    private Long expiration;

    /** Status of the person: nopass, waiting, active. */
    @Column(name = "STATUS", nullable = false)
    private StatusType status = StatusType.ACTIVE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID", nullable = true)
    private Team team;

    @ManyToMany(mappedBy = "submittedBy")
    private Set<Document> submissions = new HashSet<Document>();

    @ManyToMany(mappedBy = "evaluators")
    private Set<Document> documentsForEvaluation = new HashSet<Document>();

    // TODO - Create a separate Topics entity with many-to-many relationship
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_topics_of_interest", joinColumns = @JoinColumn(name = "USER_ID"))
    @Column(name = "TOPICS_OF_INTEREST")
    private Set<String> topicsOfInterest = new HashSet<String>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_CONFLICT_OF_INTEREST_MAPPING", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "CONFLICT_WITH_USER_ID") })
    private Set<User> myConflictsOfInterestWithUsers = new HashSet<User>();

    @ManyToMany(mappedBy = "myConflictsOfInterestWithUsers", fetch = FetchType.EAGER)
    private Set<User> usersForWhomMeInConflictOfInterest = new HashSet<User>();

    /** UserToMemberMapping many to one relationship */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<UserToMemberMapping> userToMemberMappings = new HashSet<UserToMemberMapping>();

    // user to reviewer mapping - as many to many mappings
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "submitter")
    private Set<UserToReviewerMapping> allocatedEvaluators = new HashSet<UserToReviewerMapping>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "evaluator")
    private Set<UserToReviewerMapping> submittersToEvaluate = new HashSet<UserToReviewerMapping>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<UserToReviewerMapping> getAllocatedEvaluators() {
        return allocatedEvaluators;
    }

    public void setAllocatedEvaluators(Set<UserToReviewerMapping> allocatedEvaluators) {
        this.allocatedEvaluators = allocatedEvaluators;
    }

    public boolean addAllocatedEvaluator(UserToReviewerMapping allocatedEvaluator) {
        return this.allocatedEvaluators.add(allocatedEvaluator);
    }

    public Set<UserToReviewerMapping> getSubmittersToEvaluate() {
        return submittersToEvaluate;
    }

    public void setSubmittersToEvaluate(Set<UserToReviewerMapping> submittersToEvaluate) {
        this.submittersToEvaluate = submittersToEvaluate;
    }

    public boolean addSubmitterToEvaluate(UserToReviewerMapping submitterToEvaluate) {
        return this.submittersToEvaluate.add(submitterToEvaluate);
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public Set<String> getTopicsOfInterest() {
        return topicsOfInterest;
    }

    public void setTopicsOfInterest(Set<String> topicsOfInterest) {
        this.topicsOfInterest = topicsOfInterest;
    }

    public boolean addTopicsOfInterest(String topicsOfInterest) {
        return this.topicsOfInterest.add(topicsOfInterest);
    }

    public Set<Document> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Set<Document> submissions) {
        this.submissions = submissions;
    }

    public boolean addSubmission(Document submission) {
        return this.submissions.add(submission);
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

    public Set<User> getMyConflictsOfInterestWithUsers() {
        return myConflictsOfInterestWithUsers;
    }

    public void setMyConflictsOfInterestWithUsers(Set<User> myConflictsOfInterestWithUsers) {
        this.myConflictsOfInterestWithUsers = myConflictsOfInterestWithUsers;
    }

    public boolean addMyConflictsOfInterestWithUsers(User user) {
        return this.myConflictsOfInterestWithUsers.add(user);
    }

    public Set<User> getUsersForWhomMeInConflictOfInterest() {
        return usersForWhomMeInConflictOfInterest;
    }

    public void setUsersForWhomMeInConflictOfInterest(Set<User> usersForWhomMeInConflictOfInterest) {
        this.usersForWhomMeInConflictOfInterest = usersForWhomMeInConflictOfInterest;
    }

    public boolean addUsersForWhomMeInConflictOfInterest(User user) {
        return this.usersForWhomMeInConflictOfInterest.add(user);
    }

    public Set<Document> getDocumentsForEvaluation() {
        return documentsForEvaluation;
    }

    public void setDocumentsForEvaluation(Set<Document> documentsForEvaluation) {
        this.documentsForEvaluation = documentsForEvaluation;
    }

    public boolean addDocumentForEvaluation(Document document) {
        return this.documentsForEvaluation.add(document);
    }

    /**
     * Validate the current user object
     * 
     * @return
     */
    public boolean isValidUser() {
        return false;
    }

    @Override
    public int compareTo(User o) {
        return this.id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof User) {
            User anotherUser = (User) anObject;
            return (this.id.equals(anotherUser.id) && this.email.equals(anotherUser.email) && this.username
                    .equals(anotherUser.username));
        }
        return false;
    }

    /**
     * Get user's role for given member is RoleType.EVALUATOR then returns true
     * else returns false
     * 
     * @NOTE: Use this method if "userToMemberMappings" is populated, because its lazily loaded
     * @param memberId
     * @return
     */
    public boolean isEvaluatorForMemberId(final Long memberId) {
        for (UserToMemberMapping mapping : this.userToMemberMappings) {
            if (mapping.getMember().getId().equals(memberId) && mapping.getRole() == RoleType.EVALUATOR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get user's role for given member is RoleType.CONDUCTOR then returns true
     * else returns false
     * 
     * @NOTE: Use this method if "userToMemberMappings" is populated, because its lazily loaded
     * @param memberId
     * @return
     */
    public boolean isConductorForMemberId(final Long memberId) {
        for (UserToMemberMapping mapping : this.userToMemberMappings) {
            if (mapping.getMember().getId().equals(memberId) && mapping.getRole() == RoleType.CONDUCTOR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get user's role for given member is RoleType.SUBMITTER then returns true
     * else returns false
     * 
     * @NOTE: Use this method if "userToMemberMappings" is populated, because its lazily loaded
     * @param memberId
     * @return
     */
    public boolean isSubmitterForMemberId(final Long memberId) {
        for (UserToMemberMapping mapping : this.userToMemberMappings) {
            if (mapping.getMember().getId().equals(memberId) && mapping.getRole() == RoleType.SUBMITTER) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get submitters to evaluate for given memberId
     * 
     * @NOTE: Use this method if "submittersToEvaluate" is populated, because its lazily loaded
     * @param memberId
     * @return Set<User> users which are allocated for this evaluator to
     *         evaluate for given memberId
     */
    public Set<User> getSubmittersToEvaluateForMemberId(final Long memberId) {
        Set<User> submitters = new HashSet<User>();
        for (UserToReviewerMapping mapping : this.submittersToEvaluate) {
            if (mapping.getEvaluationForMemberId().equals(memberId)) {
                submitters.add(mapping.getSubmitter());
            }
        }
        return submitters;
    }

    /* Hashcode method */
    /*
     * public int hashCode() { final int prime = 31; int hash = 17; hash = hash
     * * prime + ((int) (this.id ^ (this.id>>> 32))); return hash; }
     */
}
