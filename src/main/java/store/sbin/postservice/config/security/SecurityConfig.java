package store.sbin.postservice.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import store.sbin.postservice.config.handler.LoginFailHandler;
import store.sbin.postservice.config.handler.OAuth2FailureHandler;
import store.sbin.postservice.config.handler.OAuth2SuccessHandler;
import store.sbin.postservice.domain.Role;
import store.sbin.postservice.service.users.CustomOAuth2UserService;
import store.sbin.postservice.service.users.CustomUserDetailsService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomUserDetailsService userDetailsService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource())
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .failureHandler(new LoginFailHandler())
                        .permitAll()
                )

                // 세션 설정
                .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // 세션 필요 시 생성
                                .sessionFixation().changeSessionId() // 새로운 세션 ID 발급
                                .maximumSessions(1) // 최대 세션 수
                                .maxSessionsPreventsLogin(true) // false: 기존 세션 만료, true: 신규 세션 생성 불가
                                .expiredUrl("/login")
                );

        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                .requestMatchers(Stream
                        .of(Auth_Whitelist)
                        .map(AntPathRequestMatcher::antMatcher)
                        .toArray(AntPathRequestMatcher[]::new)).permitAll()

                // 회원가입, 로그인, 비밀번호 찾기, 이메일 인증은 인증 없이 접근 가능
                .requestMatchers(AntPathRequestMatcher.antMatcher("/user/signup/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/user/find/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/user/validation/**")).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/user/auth/**")).permitAll()
                // 게시글 목록은 인증 없이 접근 가능
                .requestMatchers(AntPathRequestMatcher.antMatcher("/post/list/**")).permitAll()
                // 인증 후 게시판 접근
                .requestMatchers(AntPathRequestMatcher.antMatcher("/post/**")).authenticated()
                // 댓글 기능 인증 후 접근
                .requestMatchers(AntPathRequestMatcher.antMatcher("/api/comment/**")).authenticated()
                // 인증된 경우 사용자 정보 변경
                .requestMatchers(AntPathRequestMatcher.antMatcher("/user/**")).authenticated()
                // 관리자 권한이 있는 경우 admin 페이지 접근
                .requestMatchers(AntPathRequestMatcher.antMatcher("/admin/**")).hasRole(Role.ADMIN.toString())
                .anyRequest().authenticated()
        );

        // OAuth2 로그인 설정
        http.oauth2Login(oAuth2Config -> oAuth2Config
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(successHandler())
                        .failureHandler(oAuth2FailureHandler())
                )
                .userDetailsService(userDetailsService)
                .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                        .logoutUrl("/logout")
                        .clearAuthentication(true) // 인증 정보 삭제
                        .invalidateHttpSession(true) // 세션 무효화
                        .logoutSuccessHandler((request, response, authentication) -> response.sendRedirect("/"))
                );

        // remember me 설정
        http.rememberMe(rememberMe -> rememberMe
                .rememberMeServices(rememberMeServices())
        );

        return http.build();
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(true);
        return rememberMeServices;
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new OAuth2SuccessHandler(customOAuth2UserService, userDetailsService);
    }

    @Bean
    public OAuth2FailureHandler oAuth2FailureHandler() {
        return new OAuth2FailureHandler();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();
        var all = Collections.singletonList(CorsConfiguration.ALL);

        configuration.setAllowedOriginPatterns(List.of("https://www.seong.store", "https://dev.seong.store"));
        configuration.setAllowedHeaders(all);
        configuration.setAllowedMethods(all);
        configuration.setMaxAge(1800L);
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(all);
        configuration.addAllowedHeader("Authorization");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    private static final String[] Auth_Whitelist = {
            "/",
            "/actuator/**",
            "/api/**",
            "/bootstrap/**",
            "/docs/**",
            "/error",
            "/favicon.ico",
            "/fonts/**",
            "/home",
            "/logout",
            "/signup",
            "/static/**",
            "/summernote/**",
            "/swagger-ui/**",
            "/test/**",
            "/user/reset/**"
    };

}
