package store.sbin.postservice.service.users;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.sbin.postservice.domain.Role;
import store.sbin.postservice.domain.User;
import store.sbin.postservice.domain.UserPrincipal;
import store.sbin.postservice.dto.UserDTO;
import store.sbin.postservice.dto.UserDTO.UserResponse;
import store.sbin.postservice.repository.UserRepository;
import store.sbin.postservice.service.files.FileService;
import store.sbin.postservice.service.post.CommentService;
import store.sbin.postservice.service.post.PostService;

import java.math.BigInteger;
import java.util.Optional;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final FileService fileService;
    private final PostService postService;
    private final CommentService commentService;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, FileService fileService, PostService postService, CommentService commentService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.fileService = fileService;
        this.postService = postService;
        this.commentService = commentService;
    }

    /**
     * 사용자 등록
     *
     * @param request 사용자 정보
     * @return 등록된 사용자 정보
     */
    public User save(UserDTO.UserRequest request) {
        String password = bCryptPasswordEncoder.encode(request.getPassword());
        userRepository.findByEmail(request.getEmail())
                .ifPresent(user -> {
                    throw new IllegalArgumentException("이미 가입된 이메일입니다.");
                });
        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(password)
                .role(request.getRole() == null ? Role.USER : request.getRole())
                .build();

        return userRepository.save(user);
    }

    /**
     * 사용자 정보 업데이트
     *
     * @param id      사용자 id
     * @param request 사용자 정보
     * @return 업데이트된 사용자 정보
     */
    public User updateUser(BigInteger id, UserDTO.UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. id = " + id));
        validateUsername(request.getUsername());
        validEmail(request.getEmail());
        user.update(request.toEntity());
        return userRepository.save(user);
    }

    /**
     * 사용자 조회
     *
     * @param id 사용자 id
     * @return 사용자 정보
     */
    public User getUserById(BigInteger id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. id = " + id));
    }

    /**
     * 이메일로 사용자 조회
     *
     * @param email 사용자 이메일
     * @return 사용자 정보
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. email = " + email));
    }

    /**
     * 사용자 목록 조회
     *
     * @param pageable 페이징 정보
     * @return 사용자 목록
     */
    public Page<UserResponse> getUserList(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponse::new);
    }

    /**
     * 사용자 삭제
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user");
        User user = getUserProfile(id);

        // 사용자 게시글 삭제
        postService.deleteAllPost(user.getId());
        // 사용자 댓글 삭제
        commentService.deleteAllComment(user.getId());
        // 사용자 삭제
        userRepository.deleteById(BigInteger.valueOf(user.getId()));
    }

    /**
     * 사용자 이름을 업데이트
     *
     * @param username 사용자 이름
     */
    @Transactional
    public void updateUsername(String username, Long id) {
        validateUsername(username);
        User user = getUserProfile(id).updateUserName(username);
        updateAuthentication(user);
        log.info("Updating username to {}", username);
    }

    /**
     * 이메일 중복 검사
     *
     * @param email 사용자 이메일
     * @return 중복 여부
     */
    public boolean validEmail(String email) {
        if (email.isBlank() || email.isEmpty()) {
            throw new IllegalArgumentException("이메일은 비워둘 수 없습니다.");
        }
        Optional<User> byEmail = userRepository.findByEmail(email);
        return byEmail.isEmpty();
    }

    /**
     * 사용자 이름 유효성 검사
     *
     * @param username 사용자 이름
     */
    private void validateUsername(String username) {
        // 비워둘 수 없음
        if (username.isBlank()) {
            throw new IllegalArgumentException("사용자 이름은 비워둘 수 없습니다.");
        }
        // 길이 제한
        if (username.length() < 2 || username.length() > 20) {
            throw new IllegalArgumentException("사용자 이름은 2자 이상 20자 이하여야 합니다.");
        }
        // 공백 포함 여부
        if (username.contains(" ")) {
            throw new IllegalArgumentException("사용자 이름에 공백이 포함되어 있습니다.");
        }
        // 영어, 한글, 숫자만 가능
        if (!username.matches("^[a-zA-Z0-9가-힣]*$")) {
            throw new IllegalArgumentException("사용자 이름은 영어, 한글, 숫자만 가능합니다.");
        }
    }

    /**
     * 프로필 이미지를 업데이트
     *
     * @param file, id
     */
    @Transactional
    public void updateProfileImage(MultipartFile file, Long id) {
        if (!file.isEmpty()) {
            removeProfileImage(id);
            User user = getUserProfile(id).updateProfileImage(fileService.storeFile(file).get("fileUrl"));
            updateAuthentication(user);
        }
    }

    @Transactional
    public void removeProfileImage(Long id) {
        log.info("Removing profile image");
        String defaultProfileImage = "/images/profile.png";
        User user = getUserProfile(id);
        Optional<String> fileUrl = Optional.ofNullable(user.getPicture());
        fileUrl.ifPresent(url -> {
            // social login profile image 삭제 안함
            if (url.startsWith("http")) return;
            fileService.deleteFile(url);
        });
        user.updateProfileImage(defaultProfileImage);
        updateAuthentication(user);
    }

    public User getUserProfile(Long id) {
        Optional<User> user = userRepository.findById(BigInteger.valueOf(id));
        log.info("Getting user profile from user id");
        return user.orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. id = " + id));
    }

    /**
     * 업데이트 된 사용자 세부 정보로 인증 객체를 업데이트
     *
     * @param user 업데이트 된 사용자
     */
    public void updateAuthentication(User user) {
        UserPrincipal newUserPrincipal = UserPrincipal.create(user);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserPrincipal userPrincipal = (UserPrincipal) principal;
        // 기존 사용자와 업데이트 된 사용자가 다른 경우
        if (!newUserPrincipal.getId().equals(userPrincipal.getId())) return;

        Object credentials = authentication.getCredentials();
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(newUserPrincipal, credentials, newUserPrincipal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    /**
     * 비밀번호 재설정
     *
     * @param request 사용자 정보
     */
    @Transactional
    public void resetPassword(UserDTO.UserRequest request) {
        User user = getUserByEmail(request.getEmail());
        user.updatePassword(bCryptPasswordEncoder.encode(request.getPassword()));
    }

    /**
     * 비밀번호 변경
     *
     * @param request 비밀번호 변경 요청
     */
    @Transactional
    public void changePassword(UserDTO.UserUpdateRequest request) {
        User user = getUserByEmail(request.getEmail());
        if (!request.getNewPw().equals(request.getNewPwConfirm())) {
            throw new IllegalArgumentException("새로운 비밀번호가 일치하지 않습니다.");
        }
        user.updatePassword(bCryptPasswordEncoder.encode(request.getNewPw()));
    }
}
