package edu.neu.ccis.sms.entity.categories;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.StatusType;
import edu.neu.ccis.sms.entity.users.User;

/**
 * Hibernate Entity bean class for UserToMemberMapping; Contain member to user
 * mappings i.e. user is registered to which member and with which role, the
 * registration status, time etc.
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 10-June-2015
 */
@Entity
@Table(name = "UserToMemberMapping", uniqueConstraints = { @UniqueConstraint(columnNames = "ID") })
public class UserToMemberMapping implements Serializable, Comparable<UserToMemberMapping> {
    private static final long serialVersionUID = -2755010302418223918L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", unique = true, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "REGISTERED_ON", nullable = false, updatable = false)
    private Date registeredOn;

    @Column(name = "ROLE", nullable = false)
    private RoleType role;

    @Column(name = "ISACTIVE", nullable = false)
    private boolean isActive = true;

    /** Status of the Member Registration : nopass, waiting, active. */
    @Column(name = "STATUS", nullable = false)
    private StatusType status = StatusType.ACTIVE;

    public UserToMemberMapping() {
        // Automatically set the registered on datetime
        registeredOn = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Date getRegisteredOn() {
        return registeredOn;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public int compareTo(UserToMemberMapping o) {
        return this.id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof UserToMemberMapping) {
            UserToMemberMapping mappings = (UserToMemberMapping) anObject;
            return (this.id.equals(mappings.id));
        }
        return false;
    }
}
