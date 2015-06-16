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
 * Hibernate Entity bean class for Post; Post contain topic, content - They are
 * ideally children to a member e.g. discussion Forum
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
@Entity
@Table(name = "Post", uniqueConstraints = { @UniqueConstraint(columnNames = "POST_ID") })
public class Post implements Serializable, Comparable<Post> {
    private static final long serialVersionUID = 1270418341201570979L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_ID", unique = true, nullable = false)
    private Long id;

    @Column(name = "POST_TOPIC")
    private String postTopic;

    @Column(name = "POST_CONTENTS")
    private String postContents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member memberCategory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPostTopic() {
        return postTopic;
    }

    public void setPostTopic(String postTopic) {
        this.postTopic = postTopic;
    }

    public String getPostContents() {
        return postContents;
    }

    public void setPostContents(String postContents) {
        this.postContents = postContents;
    }

    public Member getMemberCategory() {
        return memberCategory;
    }

    public void setMemberCategory(Member memberCategory) {
        this.memberCategory = memberCategory;
    }

    @Override
    public int compareTo(Post o) {
        return this.id.compareTo(o.getId());
    }

    @Override
    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof Post) {
            Post post = (Post) anObject;
            return (this.id.equals(post.id));
        }
        return false;
    }
}
