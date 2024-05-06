package store.sbin.postservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import store.sbin.postservice.config.BooleanToYNConverter;

import java.util.List;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("작성자")
    private String author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @Comment("제목")
    private String subject;

    @Column(columnDefinition = "TEXT")
    @Comment("내용")
    private String content;

    @Column(columnDefinition = "bigint default 0")
    @Comment("조회수")
    private Long readCount;

    @Comment("공지 여부")
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean notice;

    @JoinColumn(name = "post_id")
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("첨부파일")
    private List<Attachment> fileList;

    @Builder
    public Post(String author, String subject, String content, Boolean notice,
                List<Attachment> fileList, Long readCount, User writer) {
        this.author = author;
        this.subject = subject;
        this.content = content;
        this.fileList = fileList;
        this.notice = notice;
        this.readCount = this.readCount == null ? 0 : this.readCount;
        this.writer = writer;
    }

    public void update(String subject, String content, List<Attachment> fileList) {
        this.subject = subject;
        this.content = content;
        this.fileList = fileList;
    }

    public void update(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }
}
