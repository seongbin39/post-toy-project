package store.sbin.postservice.config.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import store.sbin.postservice.service.users.CustomOAuth2UserService;
import store.sbin.postservice.service.users.CustomUserDetailsService;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2SuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final CustomOAuth2UserService userService;

    private final CustomUserDetailsService userDetailsService;

    public OAuth2SuccessHandler(CustomOAuth2UserService customOAuth2UserService, CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.userService = customOAuth2UserService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
