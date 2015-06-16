package edu.neu.ccis.sms.entity.users;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID", nullable = true)
    private Team team;

    @ManyToMany(mappedBy = "submittedBy")
    private Set<Document> submissions = new HashSet<Document>();

    // TODO - Create a separate Topics entity with many-to-many relationship
    @ElementCollection
    @CollectionTable(name = "user_topics_of_interest", joinColumns = @JoinColumn(name = "USER_ID"))
    @Column(name = "TOPICS_OF_INTEREST")
    private Set<String> topicsOfInterest = new HashSet<String>();

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(name = "USER_CONFLICT_OF_INTEREST_MAPPING", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = { @JoinColumn(name = "CONFLICT_WITH_USER_ID") })
    private Set<User> myConflictsOfInterestWithUsers = new HashSet<User>();

    @ManyToMany(mappedBy = "myConflictsOfInterestWithUsers")
    private Set<User> usersForWhomMeInConflictOfInterest = new HashSet<User>();

    /** UserToMemberMapping many to one relationship */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<UserToMemberMapping> userToMemberMappings = new HashSet<UserToMemberMapping>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    /**
     * Validate the current user object
     * 
     * @return
     */
    public boolean isValidUser() {
        return false;
    }

    /**
     * Get the submitted document for given memberid, if there is any, otherwise
     * return null
     * 
     * @param memberId
     * @return
     */
    public Document getSubmissionDocumentForMemberId(final Long memberId) {
        for (Document submission : this.submissions) {
            if (memberId == submission.getSubmittedForMember().getId()) {
                return submission;
            }
        }
        return null;
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
}
