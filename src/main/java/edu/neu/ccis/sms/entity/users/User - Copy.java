package edu.neu.ccis.sms.entity.users;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import edu.neu.ccis.sms.entity.categories.Category;
import edu.neu.ccis.sms.entity.submissions.Document;

@Entity
@Table(name = "User", uniqueConstraints = {
        @UniqueConstraint(columnNames = "USER_ID"),
        @UniqueConstraint(columnNames = "EMAIL"),
        @UniqueConstraint(columnNames = "USERNAME") })
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "FIRSTNAME", length = 255)
    private String firstname;

    @Column(name = "LASTNAME", length = 255)
    private String lastname;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "USERNAME", nullable = false, length = 100)
    private String username;

    @Column(name = "ACTIVATION", length = 100)
    private String activation;

    @Column(name = "EXPIRATION", length = 100)
    private String expiration;

    @Column(name = "STATUS", length = 100)
    private String status;

    @Column(name = "ROLE")
    private RoleType role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEAM_ID", nullable = true)
    private Team team;

    @ManyToMany(mappedBy = "submittedBy")
    private Set<Document> submissions = new HashSet<Document>();

    @Transient
    private Set<String> topicsOfInterest = new HashSet<String>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COI_WITH_USER_ID")
    private Category conflictOfInterestWith;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "conflictOfInterestWith")
    private Set<User> conflictsOfInterest = new HashSet<User>();

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

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Category getConflictOfInterestWith() {
        return conflictOfInterestWith;
    }

    public void setConflictOfInterestWith(Category conflictOfInterestWith) {
        this.conflictOfInterestWith = conflictOfInterestWith;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public Set<String> getTopicsOfInterest() {
        return topicsOfInterest;
    }

    public void setTopicsOfInterest(Set<String> topicsOfInterest) {
        this.topicsOfInterest = topicsOfInterest;
    }

    public Set<Document> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(Set<Document> submissions) {
        this.submissions = submissions;
    }

    public Set<User> getConflictsOfInterest() {
        return conflictsOfInterest;
    }

    public void setConflictsOfInterest(Set<User> conflictsOfInterest) {
        this.conflictsOfInterest = conflictsOfInterest;
    }

    /**
     * Validate the current user object
     * 
     * @return
     */
    public boolean isValidUser() {
        return false;
    }
}
