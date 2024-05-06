package store.sbin.postservice.service.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import store.sbin.postservice.domain.SocialType;
import store.sbin.postservice.domain.User;
import store.sbin.postservice.domain.UserPrincipal;
import store.sbin.postservice.oauth2.OAuthAttributes;
import store.sbin.postservice.repository.UserRepository;

import java.util.Map;

@Service
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * OAuth2User 정보를 가져와서 UserPrincipal 객체로 변환
     *
     * @param userRequest OAuth2UserRequest
     * @return UserPrincipal
     * @throws OAuth2AuthenticationException OAuth2AuthenticationException
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        SocialType socialType = getSocialType(registrationId);

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        User savedUser = saveOrUpdate(user, socialType, userNameAttributeName);

        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    /**
     * SocialType 반환
     *
     * @param registrationId registrationId
     * @return SocialType
     */
    private SocialType getSocialType(String registrationId) {
        return switch (registrationId) {
            case "NAVER" -> SocialType.NAVER;
            case "KAKAO" -> SocialType.KAKAO;
            default -> SocialType.GOOGLE;
        };
    }

    /**
     * 사용자 정보 저장 또는 업데이트
     *
     * @param oAuth2User           OAuth2User
     * @param socialType           SocialType
     * @param userNameAttributeName userNameAttributeName
     * @return User
     */
    private User saveOrUpdate(OAuth2User oAuth2User, SocialType socialType, String userNameAttributeName) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuthAttributes oAuthAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);

        String email = oAuthAttributes.getOAuth2UserInfo().getEmail();

        User saveUser = oAuthAttributes.toEntity(socialType, oAuthAttributes.getOAuth2UserInfo());
        // 관리자 계정
        if (saveUser.getEmail().equals("seongbin3931@gmail.com")) {
            saveUser.updateAdmin();
        }

        User user = userRepository.findByEmail(email).orElse(saveUser);

        return userRepository.save(user);
    }

}
