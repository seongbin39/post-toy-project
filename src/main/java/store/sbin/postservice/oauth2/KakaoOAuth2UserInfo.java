package store.sbin.postservice.oauth2;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KakaoOAuth2UserInfo extends OAuth2UserInfo{

    private Integer id;

    public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        return String.valueOf(kakaoAccount.get("email"));
    }

    @Override
    public String getUsername() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        if (properties == null) {
            return null;
        }

        return String.valueOf(properties.get("nickname"));
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        if (properties == null) {
            return null;
        }
        return String.valueOf(properties.get("thumbnail_image"));
    }
}
