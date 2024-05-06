package store.sbin.postservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "child_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChildComment extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("댓글 ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Comment("작성자")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tagged_user_id")
    @Comment("태그된 사용자")
    private User taggedUser;

    @Column(name = "content", nullable = false)
    @Comment("내용")
    private String content;

    @Column(name = "parent_comment_id", nullable = false)
    @Comment("부모 댓글 ID")
    private Long parentCommentId;


    @Builder
    public ChildComment(Long id, User author, User taggedUser, String content, Long parentCommentId) {
        this.id = id;
        this.author = author;
        this.taggedUser = taggedUser;
        this.content = content;
        this.parentCommentId = parentCommentId;
    }

    public void update(String content) {
        this.content = content;
    }

}
