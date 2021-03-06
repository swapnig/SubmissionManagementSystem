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
 * Hibernate Entity bean class for CategoryAttribute; Contains attribute name,
 * type and value information
 *
 * @author Pramod R. Khare, Swapnil Gupta
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
@Entity
@Table(name = "CategoryAttribute", uniqueConstraints = { @UniqueConstraint(columnNames = "CATEGORY_ATTRIBUTE_ID") })
public class CategoryAttribute implements Serializable, Comparable<CategoryAttribute> {
    private static final long serialVersionUID = -9096909322421698016L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ATTRIBUTE_ID", unique = true, nullable = false)
    private Long id;

    /** Category attribute name */
    @Column(name = "NAME", unique = false, nullable = false)
    private String name;

    /** Category attribute data type - kept for future reference and development */
    @Column(name = "TYPE", unique = false, nullable = true)
    private String type;

    /** Category to which this attribute belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(final Category category) {
        this.category = category;
    }

    @Override
    public int compareTo(final CategoryAttribute o) {
        return id.compareTo(o.getId());
    }

    @Override
    public boolean equals(final Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof CategoryAttribute) {
            CategoryAttribute catAttr = (CategoryAttribute) anObject;
            return id.equals(catAttr.id);
        }
        return false;
    }
}
