package store.sbin.postservice.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.sbin.postservice.domain.User;
import store.sbin.postservice.domain.UserPrincipal;
import store.sbin.postservice.dto.SearchCondition;
import store.sbin.postservice.dto.UserDTO;
import store.sbin.postservice.dto.UserDTO.UserResponse;
import store.sbin.postservice.dto.post.PostDTO;
import store.sbin.postservice.service.post.PostService;
import store.sbin.postservice.service.users.UserService;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PostService postService;

    public AdminController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    /**
     * 관리자 페이지 메인
     *
     * @param model 모델
     * @return 관리자 페이지
     */
    @GetMapping("/main")
    public String admin(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        model.addAttribute("user", userService.getUserProfile(principal.getId()));
        return "admin/adminPage";
    }

    /**
     * 사용자 목록 조회
     *
     * @param pageable 페이징
     * @param model    모델
     * @return 사용자 목록 페이지
     */
    @GetMapping("/users/list")
    public String usersList(@PageableDefault(size = 15, page = 0, sort = "id")
                            @SortDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
            , Model model) {
        Page<UserResponse> userPage = userService.getUserList(pageable);
        model.addAttribute("userList", userPage);
        model.addAttribute("pageable", pageable);
        return "admin/users/userList";
    }

    /**
     * 사용자 정보 조회
     *
     * @param id    사용자 id
     * @param model 모델
     * @return 사용자 상세 페이지
     */
    @GetMapping("/users/{id}")
    public String getUser(@PathVariable BigInteger id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "admin/users/userDetail";
    }

    /**
     * 사용자 정보 업데이트
     *
     * @param id      사용자 id
     * @param request 사용자 정보
     * @return 사용자 목록 페이지
     */
    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable BigInteger id, UserDTO.UserRequest request) {
        User user = userService.updateUser(id, request);
        return "redirect:admin/users/list";
    }

    /**
     * 사용자 프로필 이미지 업데이트
     *
     * @param id   사용자 id
     * @param file 이미지 파일
     * @return 성공 여부
     */
    @PostMapping("/users/profileImg/{id}")
    public ResponseEntity<?> updateProfileImg(@PathVariable Long id, @RequestParam MultipartFile file) {
        userService.updateProfileImage(file, id);
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자 프로필 이미지 삭제
     *
     * @param id 사용자 id
     * @return 성공 여부
     */
    @DeleteMapping("/users/profileImg/{id}")
    public ResponseEntity<?> removeProfileImg(@PathVariable Long id) {
        userService.removeProfileImage(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 사용자 삭제
     *
     * @param id 사용자 id
     * @return 성공 여부
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    /**
     * 공지사항 목록 조회
     *
     * @param pageable        페이징
     * @param searchCondition 검색 조건
     * @param model           모델
     * @return 공지사항 목록 페이지
     */
    @GetMapping("/notice/list")
    public String noticeList(@PageableDefault(size = 15, page = 0, sort = "id")
                             @SortDefault.SortDefaults({
                                     @SortDefault(sort = "id", direction = Sort.Direction.DESC),
                             })
                             Pageable pageable,
                             SearchCondition searchCondition
            , Model model) {
        Page<PostDTO.Response> noticePage = postService.getNoticeListPage(pageable, searchCondition);
        model.addAttribute("noticeList", noticePage);
        model.addAttribute("pageable", pageable);
        model.addAttribute("searchCondition", searchCondition);
        model.addAttribute("sort", convertSortToString(pageable.getSort()));
        return "admin/notice/noticeList";
    }

    /**
     * 공지사항 상세 조회 뷰
     *
     * @return 공지사항 상세 페이지
     */
    @GetMapping("/notice/write")
    public String writeNoticeView() {
        return "admin/notice/noticeWrite";
    }

    @PostMapping("/notice/write")
    public ResponseEntity<?> writeNotice(@Valid PostDTO.Request request, BindingResult result,
                                         @RequestParam(value = "attachments", required = false) List<MultipartFile> files) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        request.setNotice(true);
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.save(request, files));
    }

    /**
     * sort 객체를 String으로 변환
     * @param sort sort 객체
     * @return 변환된 String
     */
    public String convertSortToString(Sort sort) {
        return sort.stream()
                .map(order -> order.getProperty() + "," + order.getDirection())
                .collect(Collectors.joining(","));
    }

}
