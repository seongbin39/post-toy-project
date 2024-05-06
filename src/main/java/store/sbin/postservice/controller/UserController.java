package store.sbin.postservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.sbin.postservice.domain.UserPrincipal;
import store.sbin.postservice.dto.AuthRequest;
import store.sbin.postservice.dto.UserDTO;
import store.sbin.postservice.service.AuthService;
import store.sbin.postservice.service.users.UserService;

@Controller
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    /**
     * 사용자 프로필 페이지
     *
     * @return 프로필 페이지
     */
    @GetMapping("/user/profile")
    public String userProfile(Model model) {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addAttribute("user", userService.getUserProfile(principal.getId()));
        return "content/users/profile";
    }

    /**
     * 회원 이름 변경
     *
     * @param request 변경할 이름
     * @return 프로필 페이지
     */
    @PostMapping("/user/username/{id}")
    public String updateUsername(@PathVariable Long id, @RequestBody UserDTO.UserRequest request) {
        userService.updateUsername(request.getUsername(), id);
        return "redirect:/user/profile";
    }

    /**
     * 회원 탈퇴
     *
     * @return 로그아웃 페이지
     */
    @PostMapping("/user/delete")
    public String deleteUser(UserDTO.UserRequest request) {
        userService.deleteUser(request.getId());
        return "redirect:/logout";
    }

    /**
     * 프로필 이미지 업로드
     *
     * @param file 이미지 파일
     * @return 프로필 페이지
     */
    @PostMapping("/user/image/{id}")
    public String updateProfileImage(@PathVariable Long id,
                                     @RequestParam(value = "file") MultipartFile file) {
        userService.updateProfileImage(file, id);
        return "redirect:/user/profile";
    }

    /**
     * 프로필 이미지 삭제
     *
     * @return 프로필 페이지
     */
    @DeleteMapping("/user/image/{id}")
    public String removeProfileImage(@PathVariable Long id) {
        userService.removeProfileImage(id);
        return "redirect:/user/profile";
    }

    /**
     * 회원가입
     *
     * @param request 사용자 요청
     * @return 로그인 페이지
     */
    @PostMapping("/user/signup")
    public String joinUser(@ModelAttribute UserDTO.UserRequest request) {
        userService.save(request);
        return "/content/users/login";
    }

    /**
     * 이메일 중복 체크
     *
     * @param request 사용자 요청
     * @return 중복 여부
     */
    @PostMapping("/user/validation/email")
    public ResponseEntity<Boolean> validEmail(@RequestBody UserDTO.UserRequest request) {
        return ResponseEntity.ok(userService.validEmail(request.getEmail()));
    }

    /**
     * 비밀번호 찾기
     *
     * @return 비밀번호 찾기 페이지
     */
    @GetMapping("/user/find/password")
    public String findPassword() {
        return "content/users/findPassword";
    }

    /**
     * 인증 메일 발송
     *
     * @return 성공 여부
     */
    @PostMapping("/user/auth/email")
    public ResponseEntity<String> authEmail(@RequestBody UserDTO.UserRequest request) {
        authService.sendAuthMail(request.getEmail());
        return ResponseEntity.ok("success");
    }

    /**
     * 비밀번호 찾기 인증 확인
     *
     * @return 인증 여부
     */
    @PostMapping("/user/find/password")
    public ResponseEntity<Boolean> findPassword(@RequestBody AuthRequest request) {
        boolean auth = authService.validateAuthMail(request.getEmail(), request.getAuthCode());
        if (!auth) {
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.ok(true);
    }

    /**
     * 비밀번호 재설정
     *
     * @return 성공 여부
     */
    @PostMapping("/user/reset/password")
    public ResponseEntity<Boolean> resetPassword(@RequestBody UserDTO.UserRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok(true);
    }

    @PostMapping("/user/change/password")
    public ResponseEntity<?> changePassword(@RequestBody UserDTO.UserUpdateRequest request) {
        userService.changePassword(request);

        try {
            userService.changePassword(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
