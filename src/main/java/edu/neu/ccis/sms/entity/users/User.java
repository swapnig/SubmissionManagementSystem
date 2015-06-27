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
 * User Hibernate Entity bean class; contains all important information about user, his submissions and his
 * member-registration mappings, and other personal information
 * 
 * @author Pramod R. Khare, Swapnil Gupta
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

    /** First name of user */
    @Column(name = "FIRSTNAME", length = 255, nullable = false)
    private String firstname;

    /** Last name of user */
    @Column(name = "LASTNAME", length = 255)
    private String lastname;

    /** Email id of user; which is unique and cannot be reused by other users */
    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    /** MD-5 hashed password string of this user */
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    /** username for login use; which is unique and cannot be reused by other users */
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

    /** Team for which this user is part of */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TEAM_ID", nullable = true)
    private Team team;

    /** List of all the submissions done by this for all different members */
    @ManyToMany(mappedBy = "submittedBy")
    private Set<Document> submissions = new HashSet<Document>();

    /** Documents evaluated by this user for all the members where he/she is Evaluator */
    @ManyToMany(mappedBy = "evaluators")
    private Set<Document> documentsForEvaluation = new HashSet<Document>();

    /** Topics of preference or interest - currently stored as list of strings */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "UserTopicsOfInterest", joinColumns = @JoinColumn(name = "USER_ID"))
    @Column(name = "TOPICS_OF_INTEREST")
    private Set<String> topicsOfInterest = new HashSet<String>();

    /** List of users with whom this user has conflicts of interest with */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "UserConflictOfInterestMapping", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "CONFLICT_WITH_USER_ID") })
    private Set<User> myConflictsOfInterestWithUsers = new HashSet<User>();

    /** List of users who have marked this user in their conflicts of interest list */
    @ManyToMany(mappedBy = "myConflictsOfInterestWithUsers", fetch = FetchType.EAGER)
    private Set<User> usersForWhomMeInConflictOfInterest = new HashSet<User>();

    /** List of all registrations by this User to different members */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<UserToMemberMapping> userToMemberMappings = new HashSet<UserToMemberMapping>();

    /** user to reviewer mapping -i.e. for different members who were the evaluators mapped to this user's submissions */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "submitter")
    private Set<UserToReviewerMapping> allocatedEvaluators = new HashSet<UserToReviewerMapping>();

    /**
     * List of submitters to evaluators for different members whom this user has to evaluate i.e. for all members where
     * this user is has Evaluator role
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "evaluator")
    private Set<UserToReviewerMapping> submittersToEvaluate = new HashSet<UserToReviewerMapping>();

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Set<UserToReviewerMapping> getAllocatedEvaluators() {
        return allocatedEvaluators;
    }

    public void setAllocatedEvaluators(final Set<UserToReviewerMapping> allocatedEvaluators) {
        this.allocatedEvaluators = allocatedEvaluators;
    }

    public boolean addAllocatedEvaluator(final UserToReviewerMapping allocatedEvaluator) {
        return allocatedEvaluators.add(allocatedEvaluator);
    }

    public Set<UserToReviewerMapping> getSubmittersToEvaluate() {
        return submittersToEvaluate;
    }

    public void setSubmittersToEvaluate(final Set<UserToReviewerMapping> submittersToEvaluate) {
        this.submittersToEvaluate = submittersToEvaluate;
    }

    public boolean addSubmitterToEvaluate(final UserToReviewerMapping submitterToEvaluate) {
        return submittersToEvaluate.add(submitterToEvaluate);
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(final Team team) {
        this.team = team;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(final String activation) {
        this.activation = activation;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(final Long expiration) {
        this.expiration = expiration;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(final StatusType status) {
        this.status = status;
    }

    public Set<String> getTopicsOfInterest() {
        return topicsOfInterest;
    }

    public void setTopicsOfInterest(final Set<String> topicsOfInterest) {
        this.topicsOfInterest = topicsOfInterest;
    }

    public boolean addTopicsOfInterest(final String topicsOfInterest) {
        return this.topicsOfInterest.add(topicsOfInterest);
    }

    public Set<Document> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(final Set<Document> submissions) {
        this.submissions = submissions;
    }

    public boolean addSubmission(final Document submission) {
        return submissions.add(submission);
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

    public Set<User> getMyConflictsOfInterestWithUsers() {
        return myConflictsOfInterestWithUsers;
    }

    public void setMyConflictsOfInterestWithUsers(final Set<User> myConflictsOfInterestWithUsers) {
        this.myConflictsOfInterestWithUsers = myConflictsOfInterestWithUsers;
    }

    public boolean addMyConflictsOfInterestWithUsers(final User user) {
        return myConflictsOfInterestWithUsers.add(user);
    }

    public Set<User> getUsersForWhomMeInConflictOfInterest() {
        return usersForWhomMeInConflictOfInterest;
    }

    public void setUsersForWhomMeInConflictOfInterest(final Set<User> usersForWhomMeInConflictOfInterest) {
        this.usersForWhomMeInConflictOfInterest = usersForWhomMeInConflictOfInterest;
    }

    public boolean addUsersForWhomMeInConflictOfInterest(final User user) {
        return usersForWhomMeInConflictOfInterest.add(user);
    }

    public Set<Document> getDocumentsForEvaluation() {
        return documentsForEvaluation;
    }

    public void setDocumentsForEvaluation(final Set<Document> documentsForEvaluation) {
        this.documentsForEvaluation = documentsForEvaluation;
    }

    public boolean addDocumentForEvaluation(final Document document) {
        return documentsForEvaluation.add(document);
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
    public int compareTo(final User o) {
        return id.compareTo(o.getId());
    }

    @Override
    public boolean equals(final Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof User) {
            User anotherUser = (User) anObject;
            return id.equals(anotherUser.id) && email.equals(anotherUser.email) && username
                    .equals(anotherUser.username);
        }
        return false;
    }

    /**
     * Get user's role for given member is RoleType.EVALUATOR then returns true else returns false
     * 
     * @NOTE: Use this method if "userToMemberMappings" is populated, because its lazily loaded
     * @param memberId
     * @return
     */
    public boolean isEvaluatorForMemberId(final Long memberId) {
        for (UserToMemberMapping mapping : userToMemberMappings) {
            if (mapping.getMember().getId().equals(memberId) && mapping.getRole() == RoleType.EVALUATOR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get user's role for given member is RoleType.CONDUCTOR then returns true else returns false
     * 
     * @NOTE: Use this method if "userToMemberMappings" is populated, because its lazily loaded
     * @param memberId
     * @return
     */
    public boolean isConductorForMemberId(final Long memberId) {
        for (UserToMemberMapping mapping : userToMemberMappings) {
            if (mapping.getMember().getId().equals(memberId) && mapping.getRole() == RoleType.CONDUCTOR) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get user's role for given member is RoleType.SUBMITTER then returns true else returns false
     * 
     * @NOTE: Use this method if "userToMemberMappings" is populated, because its lazily loaded
     * @param memberId
     * @return
     */
    public boolean isSubmitterForMemberId(final Long memberId) {
        for (UserToMemberMapping mapping : userToMemberMappings) {
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
     * @return Set<User> users which are allocated for this evaluator to evaluate for given memberId
     */
    public Set<User> getSubmittersToEvaluateForMemberId(final Long memberId) {
        Set<User> submitters = new HashSet<User>();
        for (UserToReviewerMapping mapping : submittersToEvaluate) {
            if (mapping.getEvaluationForMemberId().equals(memberId)) {
                submitters.add(mapping.getSubmitter());
            }
        }
        return submitters;
    }

    /* Hashcode method */
    /*
     * public int hashCode() { final int prime = 31; int hash = 17; hash = hash * prime + ((int) (this.id ^ (this.id>>>
     * 32))); return hash; }
     */
}
