package store.sbin.postservice.oauth2;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class NaverOAuth2UserInfo extends OAuth2UserInfo{

    public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
//        Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
    }

    @Override
    public String getId() {
        Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
        return String.valueOf(naverResponse.get("id"));
//        return attributes.get("id");
    }

    @Override
    public String getEmail() {
        Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");

        return String.valueOf(naverResponse.get("email"));
    }

    @Override
    public String getUsername() {
        Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
        return String.valueOf(naverResponse.get("nickname"));
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> naverResponse = (Map<String, Object>) attributes.get("response");
        return String.valueOf(naverResponse.get("profile_image"));
    }
}
