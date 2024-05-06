package store.sbin.postservice.oauth2;

import store.sbin.postservice.domain.SocialType;

import java.util.Map;

public class OAuth2UserInfoProvider {
    public static OAuth2UserInfo getOAuth2UserInfo(SocialType socialType, Map<String, Object> attributes) {
        switch (socialType) {
            case GOOGLE -> {
                return new GoogleOAuth2UserInfo(attributes);
            }
            case NAVER -> {
                return new NaverOAuth2UserInfo(attributes);
            }
            case KAKAO -> {
                return new KakaoOAuth2UserInfo(attributes);
            }
//            case APPLE -> {
//            }
//            case GITHUB -> {
//            }
            default -> throw new IllegalArgumentException("제공되지 않는 소셜 서비스 입니다.");
        }
    }
}
