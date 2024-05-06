package store.sbin.postservice.dto.post;

import lombok.*;
import store.sbin.postservice.domain.Comments;
import store.sbin.postservice.domain.User;
import store.sbin.postservice.dto.UserDTO.UserResponse;

import java.time.LocalDateTime;

public class CommentDTO {

    @Getter
    @Setter
    public static class CommentRequest {
        private Long id;
        private User author;
        private Long userId;
        private String content;
        private Long postId;
        private Long replyCount;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;

        public Comments toEntity() {
            return Comments.builder()
                    .author(author)
                    .content(content)
                    .postId(postId)
                    .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentResponse {
        private Long id;
        private UserResponse author;
        private Long userId;
        private String content;
        private Long postId;
        private Long replyCount;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;

        @Builder
        public CommentResponse(Comments comments) {
            this.id = comments.getId();
            this.author = new UserResponse(comments.getAuthor());
            this.userId = comments.getAuthor().getId();
            this.content = comments.getContent();
            this.postId = comments.getPostId();
            this.createdDate = comments.getCreatedDate();
            this.modifiedDate = comments.getModifiedDate();
        }
    }
}
