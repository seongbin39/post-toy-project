package store.sbin.postservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.sbin.postservice.domain.Role;
import store.sbin.postservice.domain.User;

import java.time.LocalDateTime;

public class UserDTO {

    /**
     * UserRequest
     * 사용자 정보 요청 DTO
     */
    @Getter
    @Setter
    public static class UserRequest {
        private Long id;
        private String email;
        private String username;
        private String password;
        private Role role;

        @Builder
        public UserRequest(String email, String username, String password, Role role){
            this.email = email;
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .username(username)
                    .role(role)
                    .build();
        }
    }

    /**
     * UserResponse
     * 사용자 정보 응답 DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserResponse {
        private Long id;
        private String email;
        private String username;
        private String picture;
        private Role role;
        private LocalDateTime createdDate;
        private LocalDateTime modifiedDate;

        @Builder
        public UserResponse(User user){
            this.id = user.getId();
            this.email = user.getEmail();
            this.username = user.getUsername();
            this.picture = user.getPicture();
            this.role = user.getRole();
            this.createdDate = user.getCreatedDate();
            this.modifiedDate = user.getModifiedDate();
        }
    }

    /**
     * UserUpdateRequest
     * 사용자 정보 수정 요청 DTO
     */
    @Getter
    @Setter
    public static class UserUpdateRequest {
        private String email;
        private String currentPw;
        private String newPw;
        private String newPwConfirm;

        @Builder
        public UserUpdateRequest(String email, String currentPw, String newPw, String newPwConfirm){
            this.email = email;
            this.currentPw = currentPw;
            this.newPw = newPw;
            this.newPwConfirm = newPwConfirm;
        }
    }
}
