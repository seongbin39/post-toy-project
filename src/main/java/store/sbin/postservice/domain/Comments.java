package store.sbin.postservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comments extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("댓글 ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @Comment("작성자")
    private User author;

    @Comment("내용")
    private String content;

    @Column(name = "post_id", nullable = false)
    @Comment("게시글 ID")
    private Long postId;

    @Builder
    public Comments(User author, String content, Long postId) {
        this.author = author;
        this.content = content;
        this.postId = postId;
    }
    public void update(String content) {
        this.content = content;
    }
}
