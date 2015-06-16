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

@Entity
@Table(name = "CategoryAttribute", uniqueConstraints = { @UniqueConstraint(columnNames = "CATEGORY_ATTRIBUTE_ID") })
public class CategoryAttribute implements Serializable, Comparable<CategoryAttribute> {
    private static final long serialVersionUID = -9096909322421698016L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CATEGORY_ATTRIBUTE_ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "NAME", unique = false, nullable = false)
    private String name;

    @Column(name = "TYPE", unique = false, nullable = true)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", nullable = false)
    private Category category;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    @Override
    public int compareTo(CategoryAttribute o) {
        return this.id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof CategoryAttribute) {
            CategoryAttribute catAttr = (CategoryAttribute) anObject;
            return (this.id.equals(catAttr.id));
        }
        return false;
    }
}
