package store.sbin.postservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.sbin.postservice.dto.post.ChildCommentDTO;
import store.sbin.postservice.dto.post.CommentDTO;
import store.sbin.postservice.service.post.CommentService;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 댓글 목록 조회
     * @return ResponseEntity<List<CommentDTO.CommentResponse>> 댓글 목록
     */
    @GetMapping("/comment/{postId}")
    public ResponseEntity<List<CommentDTO.CommentResponse>> getCommentList(@PathVariable Long postId) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentList(postId));
    }

    /**
     * 댓글 등록
     * @return ResponseEntity<CommentDTO.CommentResponse> 댓글 등록 결과
     */
    @PostMapping("/comment")
    public ResponseEntity<CommentDTO.CommentResponse> createComment(@RequestBody CommentDTO.CommentRequest commentDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.save(commentDto));
    }

    /**
     * 댓글 수정
     * @return ResponseEntity<CommentDTO.CommentResponse> 댓글 수정 결과
     */
    @PostMapping("/comment/{id}")
    public ResponseEntity<CommentDTO.CommentResponse> updateComment(@PathVariable Long id, @RequestBody CommentDTO.CommentRequest commentDto) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.update(id, commentDto));
    }

    /**
     * 댓글 삭제
     * @return ResponseEntity<String> 댓글 삭제 결과
     */
    @DeleteMapping("/comment/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 대댓글 등록
     * @return ResponseEntity<ChildCommentDTO.ReplyResponse> 대댓글 등록 결과
     */
    @PostMapping("/reply")
    public ResponseEntity<ChildCommentDTO.ReplyResponse> createReply(@RequestBody ChildCommentDTO.ReplyRequest replyRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.saveReply(replyRequest));
    }

    /**
     * 대댓글 목록 조회
     * @return ResponseEntity<List<ChildCommentDTO.ReplyResponse>> 대댓글 목록
     */
    @PostMapping("/reply/{parentId}")
    public ResponseEntity<List<ChildCommentDTO.ReplyResponse>> getReplyList(@PathVariable Long parentId) {
        List<ChildCommentDTO.ReplyResponse> replyList = commentService.getReplyList(parentId);
        return ResponseEntity.status(HttpStatus.OK).body(replyList);
    }

    /**
     * 대댓글 수정
     * @return ResponseEntity<ChildCommentDTO.ReplyResponse> 대댓글 수정 결과
     */
    @PostMapping("/reply/update/{id}")
    public ResponseEntity<ChildCommentDTO.ReplyResponse> updateReply(@PathVariable Long id, @RequestBody ChildCommentDTO.ReplyRequest replyRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(commentService.updateReply(id, replyRequest));
    }

    /**
     * 대댓글 삭제
     * @return ResponseEntity<String> 대댓글 삭제 결과
     */
    @PostMapping("/reply/delete/{id}")
    public ResponseEntity<String> deleteReply(@PathVariable Long id) {
        commentService.deleteReply(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 대댓글 목록 조회
     * @return ResponseEntity<List<ChildCommentDTO.ReplyResponse>> 대댓글 목록
     */
    @GetMapping("/reply/{parentId}")
    public String replyList(@PathVariable Long parentId, Model model) {
        List<ChildCommentDTO.ReplyResponse> replyList = commentService.getReplyList(parentId);
        model.addAttribute("parentCommentId", parentId);
        model.addAttribute("replyList", replyList);
        return "content/posts/reply :: #" + "reply";
    }

}
