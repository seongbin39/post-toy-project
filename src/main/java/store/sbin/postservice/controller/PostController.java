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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.sbin.postservice.dto.SearchCondition;
import store.sbin.postservice.dto.post.AttachmentDTO;
import store.sbin.postservice.dto.post.CommentDTO;
import store.sbin.postservice.dto.post.PostDTO;
import store.sbin.postservice.service.post.CommentService;
import store.sbin.postservice.service.post.PostService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequestMapping("/post")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    /**
     * 게시글 목록 조회
     *
     * @param pageable 페이징 정보
     * @return 게시글 목록 페이지
     */
    @GetMapping("/list")
    public String getPostPage(
            SearchCondition searchCondition,
            @PageableDefault(size = 15, page = 0, sort = "id")
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC),
            })
            Pageable pageable,
            Model model
    ) {
        Page<PostDTO.Response> postList = postService.getPostList(pageable, searchCondition);

        model.addAttribute("postList", postList);
        model.addAttribute("searchCondition", searchCondition);
        model.addAttribute("sort", convertSortToString(pageable.getSort()));

        return "content/posts/postList";
    }

    @PostMapping("/recentList")
    public ResponseEntity<List<PostDTO.Response>> getRecentPostList() {
        return ResponseEntity.ok(postService.getRecentPostList());
    }
    /**
     * 게시글 작성 페이지 이동
     *
     * @return 게시글 작성 페이지
     */
    @GetMapping("/write")
    public String writePost() {
        return "content/posts/postWrite";
    }

    /**
     * 게시글 작성
     *
     * @param postDto 게시글 정보
     * @param result  검증 결과
     * @param files   첨부파일
     * @return 작성 결과
     */
    @PostMapping("/write")
    public ResponseEntity<?> writePost(@Valid PostDTO.Request postDto, BindingResult result
            , @RequestParam(value = "attachments", required = false) List<MultipartFile> files
    ) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(postService.save(postDto, files));
    }

    /**
     * 게시글 상세 조회
     *
     * @param model         Model 객체
     * @param id            게시글 ID
     * @param searchCondition 검색 조건
     * @param pageable      페이징 정보
     * @return 게시글 상세 페이지
     */
    @GetMapping("/{id}")
    public String getPost(Model model, @PathVariable Long id, SearchCondition searchCondition, Pageable pageable) {
        // 조회수 증가
        postService.updateReadCount(id);

        PostDTO.Response postDto = postService.getPostDetail(id); // 게시글 상세 조회
        List<AttachmentDTO> attachmentList = postService.getAttachmentList(id); // 첨부파일 목록 조회
        List<CommentDTO.CommentResponse> commentList = commentService.getCommentList(id); // 댓글 목록 조회

        model.addAllAttributes(Map.of(
                "post", postDto,
                "attachmentList", attachmentList,
                "commentList", commentList,
                "searchCondition", searchCondition,
                "pageable", pageable,
                "sort", convertSortToString(pageable.getSort())
        ));

        return "content/posts/postDetail";
    }

    /**
     * 게시글 수정 페이지 이동
     *
     * @param model Model 객체
     * @param id    게시글 ID
     * @return 게시글 수정 페이지
     */
    @GetMapping("/update/{id}")
    public String updatePost(Model model, @PathVariable Long id, SearchCondition searchCondition, Pageable pageable) {
        PostDTO.Response postDto = postService.getPostDetail(id);
        List<AttachmentDTO> attachmentList = postService.getAttachmentList(id);

        model.addAllAttributes(Map.of(
                "post", postDto,
                "attachmentList", attachmentList,
                "searchCondition", searchCondition,
                "pageable", pageable
        ));

        return "content/posts/postUpdate";
    }

    /**
     * 게시글 수정
     *
     * @param postDto     수정할 게시글 정보
     * @param files       새로 추가할 첨부파일
     * @param deleteFiles 삭제할 첨부파일
     * @param id          게시글 ID
     * @return 수정 결과
     */
    @PostMapping("/update/{id}")
    public ResponseEntity<String> updatePost(PostDTO.Request postDto
            , @RequestParam(value = "attachments", required = false) List<MultipartFile> files
            , @RequestParam(value = "deleteFiles", required = false) List<Long> deleteFiles
            , @PathVariable Long id) {

        postService.update(id, postDto, files, deleteFiles);

        return ResponseEntity.ok().build();
    }

    /**
     * 게시글 삭제
     *
     * @param id 삭제할 게시글 ID
     * @return 삭제 결과
     */
    @PostMapping("/delete/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.delete(id);
        return "redirect:/post/list";
    }

    public String convertSortToString(Sort sort) {
        return sort.stream()
                .map(order -> order.getProperty() + "," + order.getDirection())
                .collect(Collectors.joining(","));
    }

}
