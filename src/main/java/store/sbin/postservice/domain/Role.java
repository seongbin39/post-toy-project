package store.sbin.postservice.domain;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Role {

    GUEST("ROLE_GUEST", "게스트 권한"),
    USER("ROLE_USER", "일반 사용자 권한"),
    ADMIN("ROLE_ADMIN", "관리자 권한");

    private String code;
    private String roleName;

    Role(String code, String roleName) {
        this.code = code;
        this.roleName = roleName;
    }

    public static Role of(String code) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getCode().equals(code))
                .findAny()
                .orElse(GUEST);
    }

}
