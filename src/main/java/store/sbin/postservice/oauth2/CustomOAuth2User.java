package store.sbin.postservice.oauth2;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import store.sbin.postservice.domain.Role;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private String email;
    private Role role;

    /**
     * Constructs a {@code DefaultOAuth2User} using the provided parameters.
     */
    public CustomOAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey,
            String email,
            Role role
    ) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.role = role;
    }
}
