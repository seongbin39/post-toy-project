package store.sbin.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import store.sbin.postservice.domain.Attachment;
import store.sbin.postservice.domain.Post;
import store.sbin.postservice.domain.User;
import store.sbin.postservice.dto.UserDTO.UserResponse;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {

    /**
     * PostRequest
     * 게시글 작성 요청 DTO
     */
    @Getter
    @Setter
    public static class Request {
        private User author;
        private Long userId;
        @NotBlank(message = "제목은 필수 입력사항 입니다.")
        @Length(min = 1, max = 100, message = "제목은 1자 이상 100자 이하로 입력해주세요.")
        private String subject;
        @NotBlank(message = "내용을 입력해주세요.")
        @Length(min = 1, max = 3000, message = "내용은 1자 이상 3000자 이하로 입력해주세요.")
        private String content;
        private Long readCount;
        private List<Attachment> files;
        private Boolean notice;

        public Post toEntity() {
            return Post.builder()
                    .writer(author)
                    .subject(subject)
                    .content(content)
                    .fileList(files)
                    .notice(notice)
                    .readCount(readCount == null ? 0 : readCount)
                    .build();
        }
    }

    /**
     * PostResponse
     * 게시글 상세 응답 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private UserResponse author;
        private String subject;
        private String content;
        private Long readCount;
        private Boolean notice;
        private List<Attachment> files;
        private Long attachCount;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;

        @Builder
        public Response(Post post) {
            this.id = post.getId();
            this.author = new UserResponse(post.getWriter());
            this.subject = post.getSubject();
            this.content = post.getContent();
            this.readCount = post.getReadCount();
            this.notice = post.getNotice();
            this.createdDate = post.getCreatedDate();
            this.modifiedDate = post.getModifiedDate();
        }
    }

}
