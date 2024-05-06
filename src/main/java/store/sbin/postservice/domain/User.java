package store.sbin.postservice.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    @Comment("이메일")
    private String email;

    @Column(name = "username", nullable = false)
    @Comment("사용자명")
    private String username;

    @Column(name = "password")
    @Comment("비밀번호")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    @Comment("역할")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type")
    @Comment("소셜 서비스명")
    private SocialType socialType;

    @Column(name = "social_Id")
    @Comment("소셜 ID")
    private String socialId;

    @Column(name = "picture")
    @Comment("프로필 이미지")
    private String picture;

    @Builder
    public User(String email, String username, String password, Role role, SocialType socialType, String socialId, String picture) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.socialType = socialType;
        this.socialId = socialId;
        this.picture = picture;
    }

    public User updateUserName(String username){
        this.username = username;
        return this;
    }

    public User updateAdmin(){
        this.role = Role.ADMIN;
        return this;
    }

    public User updateRole(Role role){
        this.role = role;
        return this;
    }

    public User updateEmail(String email) {
        this.email = email;
        return this;
    }

    public User updateProfileImage(String picture) {
        this.picture = picture;
        return this;
    }

    public User update(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.picture = user.getPicture();
        return this;
    }

    public void updatePassword(String encode) {
        this.password = encode;
    }
}
