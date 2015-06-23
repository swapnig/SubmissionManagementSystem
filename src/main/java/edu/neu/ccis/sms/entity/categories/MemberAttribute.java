package edu.neu.ccis.sms.entity.categories;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Hibernate Entity bean class for MemberAttribute; Contains attribute name, type and value information and which member
 * it belongs to.
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
@Entity
@Table(name = "MemberAttribute", uniqueConstraints = { @UniqueConstraint(columnNames = "MEMBER_ATTRIBUTE_ID") })
public class MemberAttribute implements Serializable, Comparable<MemberAttribute> {
    private static final long serialVersionUID = -3603646957361127023L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEMBER_ATTRIBUTE_ID", unique = true, nullable = false)
    private Long id;

    /** Member attribute name */
    @Column(name = "NAME", unique = false, nullable = false)
    private String name;

    /** Member attribute value */
    @Column(name = "VALUE", unique = false, nullable = true)
    private String value;

    /** member to which this attribute belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    public MemberAttribute() {
    }

    public MemberAttribute(final String attributeName, final String attributeValue, final Member member) {
        this.name = attributeName;
        this.value = attributeValue;
        this.member = member;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    @Override
    public int compareTo(MemberAttribute o) {
        return this.id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof MemberAttribute) {
            MemberAttribute memAttr = (MemberAttribute) anObject;
            return (this.id.equals(memAttr.id));
        }
        return false;
    }
}
