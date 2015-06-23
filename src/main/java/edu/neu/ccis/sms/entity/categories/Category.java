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

/**
 * Hibernate Entity bean class for Category; Contains category attributes and other category parameters
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
@Entity
@Table(name = "Category", uniqueConstraints = { @UniqueConstraint(columnNames = "CATEGORY_ID"),
        @UniqueConstraint(columnNames = "CATEGORY_NAME") })
public class Category implements Serializable, Comparable<Category> {
    private static final long serialVersionUID = 3458910199790033297L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ID", unique = true, nullable = false)
    private Long id;

    /** Category name - which is unique for a given installation */
    @Column(name = "CATEGORY_NAME", nullable = false)
    private String name;

    /**
     * boolean flag indicating is the members created out of this category are registerable members i.e. to which users
     * can register to
     */
    @Column(name = "IS_REGISTERABLE", nullable = false)
    private boolean isRegisterable = false;

    /**
     * boolean flag indicating is the members created out of this category are submittable members i.e. to which users
     * can submit their assignments to
     */
    @Column(name = "IS_SUBMITTABLE", nullable = false)
    private boolean isSubmittable = false;

    /** Parent Category in the category hierarchy tree */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_CATEGORY_ID")
    private Category parentCategory;

    /**
     * List of children categories to which this category is parent category - currently we only support single parent
     * i.e. a node cannot have multiple parent categories
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentCategory")
    private Set<Category> childCategories = new HashSet<Category>();

    /** Attributes list which store extra information about this category */
    @OneToMany(fetch = FetchType.EAGER, cascade = { CascadeType.ALL, CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "category")
    @Column(nullable = false)
    private Set<CategoryAttribute> attributes = new HashSet<CategoryAttribute>();

    /** list of members which were created out of this category */
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "category", cascade = CascadeType.ALL)
    private Set<Member> members = new HashSet<Member>();

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

    public Category getParentCategory() {
        return parentCategory;
    }

    public void setParentCategory(Category parentCategory) {
        this.parentCategory = parentCategory;
    }

    public Set<Category> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(Set<Category> childCategories) {
        this.childCategories = childCategories;
    }

    public boolean addChildCategories(Category childCategorie) {
        return this.childCategories.add(childCategorie);
    }

    public Set<CategoryAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<CategoryAttribute> attributes) {
        this.attributes = attributes;
    }

    public boolean addAttributes(CategoryAttribute attribute) {
        return this.attributes.add(attribute);
    }

    public Set<Member> getMembers() {
        return members;
    }

    public void setMembers(Set<Member> members) {
        this.members = members;
    }

    public boolean addMembers(Member member) {
        return this.members.add(member);
    }

    @Override
    public int compareTo(Category o) {
        return this.id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Category) {
            Category cat = (Category) anObject;
            return (this.id.equals(cat.id) && this.name.equals(cat.name));
        }
        return false;
    }
}
