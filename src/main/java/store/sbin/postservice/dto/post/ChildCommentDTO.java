package store.sbin.postservice.dto.post;

import lombok.*;
import store.sbin.postservice.domain.ChildComment;
import store.sbin.postservice.domain.User;
import store.sbin.postservice.dto.UserDTO.UserResponse;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChildCommentDTO {

    @Getter
    @Setter
    public static class ReplyRequest {
        private Long id;
        private User author;
        private Long userId;
        private User taggedUser;
        private Long taggedUserId;
        private String content;
        private Long parentCommentId;

        public ChildComment toEntity() {
            return ChildComment.builder()
                .id(this.id)
                .author(this.author)
                .taggedUser(this.taggedUser)
                .content(this.content)
                .parentCommentId(this.parentCommentId)
                .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyResponse {
        private Long id;
        private UserResponse author;
        private Long userId;
        private UserResponse taggedUser;
        private Long taggedUserId;
        private String content;
        private Long parentCommentId;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;

        @Builder
        public ReplyResponse(ChildComment childComment) {
            this.id = childComment.getId();
            this.author = new UserResponse(childComment.getAuthor());
            this.userId = childComment.getAuthor().getId();
            this.taggedUser = childComment.getTaggedUser() == null ? null : new UserResponse(childComment.getTaggedUser());
            this.taggedUserId = childComment.getTaggedUser() == null ? null : childComment.getTaggedUser().getId();
            this.content = childComment.getContent();
            this.parentCommentId = childComment.getParentCommentId();
            this.createdDate = childComment.getCreatedDate();
            this.modifiedDate = childComment.getModifiedDate();
        }

    }
}
