package store.sbin.postservice.oauth2;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import store.sbin.postservice.domain.Role;
import store.sbin.postservice.domain.SocialType;
import store.sbin.postservice.domain.User;

import java.util.Map;

@Getter
@Slf4j
public class OAuthAttributes {

    private final String attributeKey;
    private final OAuth2UserInfo oAuth2UserInfo;

    @Builder
    public OAuthAttributes(String attributeKey, OAuth2UserInfo oAuth2UserInfo) {
        this.attributeKey = attributeKey;
        this.oAuth2UserInfo = oAuth2UserInfo;
    }

    public static OAuthAttributes of(SocialType socialType, String attributeKey, Map<String, Object> attributes){
        if (socialType.equals(SocialType.GOOGLE)){
            return ofGoogle(attributeKey, attributes);
        } else if (socialType.equals(SocialType.KAKAO)){
            return ofKakao(attributeKey, attributes);
        } else if (socialType.equals(SocialType.NAVER)){
            return ofNaver(attributeKey, attributes);
        }
        return ofGoogle(attributeKey, attributes);
    }

    private static OAuthAttributes ofNaver(String attributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .attributeKey(attributeKey)
                .oAuth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofKakao(String attributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .attributeKey(attributeKey)
                .oAuth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    private static OAuthAttributes ofGoogle(String attributeKey, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .attributeKey(attributeKey)
                .oAuth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public User toEntity(SocialType socialType, OAuth2UserInfo oAuth2UserInfo){
        return User.builder()
                .email(oAuth2UserInfo.getEmail())
                .username(oAuth2UserInfo.getUsername())
                .socialId(oAuth2UserInfo.getId())
                .socialType(socialType)
                .picture(oAuth2UserInfo.getImageUrl())
                .role(Role.USER)
                .build();
    }

}
