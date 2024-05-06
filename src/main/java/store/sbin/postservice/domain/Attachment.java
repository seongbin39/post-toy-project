package store.sbin.postservice.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attachment extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("파일명")
    private String fileName;
    @Comment("원본파일명")
    private String fileOriName;
    @Comment("파일경로")
    private String fileUrl;
    @Comment("파일타입")
    private String fileType;
    @Comment("파일크기")
    private Long fileSize;

    @Column(name = "post_id")
    @Comment("게시글 ID")
    private Long postId;

    @Builder
    public Attachment(Long id, String fileName, String fileOriName, String fileUrl, String fileType, Long fileSize, Long postId) {
        this.id = id;
        this.fileName = fileName;
        this.fileOriName = fileOriName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.postId = postId;
    }

}
