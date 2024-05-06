package store.sbin.postservice.service.post;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.sbin.postservice.domain.ChildComment;
import store.sbin.postservice.domain.Comments;
import store.sbin.postservice.domain.User;
import store.sbin.postservice.dto.post.ChildCommentDTO;
import store.sbin.postservice.dto.post.CommentDTO;
import store.sbin.postservice.repository.UserRepository;
import store.sbin.postservice.repository.post.ChildCommentRepository;
import store.sbin.postservice.repository.post.CommentRepository;
import store.sbin.postservice.repository.post.CommentRepositoryImpl;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentRepositoryImpl commentRepositoryImpl;
    private final ChildCommentRepository childCommentRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, CommentRepositoryImpl commentRepositoryImpl, ChildCommentRepository childCommentRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.commentRepositoryImpl = commentRepositoryImpl;
        this.childCommentRepository = childCommentRepository;
        this.userRepository = userRepository;
    }

    /**
     * 댓글 리스트 조회
     *
     * @param postId 게시글 ID
     * @return 댓글 리스트
     */
    public List<CommentDTO.CommentResponse> getCommentList(Long postId) {
        return commentRepositoryImpl.findAllByPostId(postId);
    }

    /**
     * 댓글 수정
     *
     * @param commentDto 댓글 정보
     * @return 수정된 댓글 정보
     */
    @Transactional
    public CommentDTO.CommentResponse update(Long id, CommentDTO.CommentRequest commentDto) {
        Comments comments = commentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + id));
        comments.update(commentDto.getContent());

        return CommentDTO.CommentResponse.builder()
                .comments(comments)
                .build();
    }


    /**
     * 댓글 저장
     *
     * @param commentDto 댓글 정보
     * @return 저장된 댓글 정보
     */
    public CommentDTO.CommentResponse save(CommentDTO.CommentRequest commentDto) {
        // 작성자 정보 설정
        Optional<User> user = userRepository.findById(BigInteger.valueOf(commentDto.getUserId()));
        user.ifPresent(commentDto::setAuthor);
        user.orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. id=" + commentDto.getUserId()));

        Comments comments = commentRepository.save(commentDto.toEntity());

        return CommentDTO.CommentResponse.builder()
                .comments(comments)
                .build();
    }

    /**
     * 대댓글 저장
     *
     * @param replyRequest 대댓글 정보
     * @return 저장된 대댓글 정보
     */
    @Transactional
    public ChildCommentDTO.ReplyResponse saveReply(ChildCommentDTO.ReplyRequest replyRequest) {
        // 작성자 정보 설정
        Optional<User> user = userRepository.findById(BigInteger.valueOf(replyRequest.getUserId()));
        user.ifPresent(replyRequest::setAuthor);
        user.orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. id=" + replyRequest.getUserId()));

        // 태그된 사용자 정보 설정
        if (replyRequest.getTaggedUserId() != null) {
            Optional<User> taggedUser = userRepository.findById(BigInteger.valueOf(replyRequest.getTaggedUserId()));
            taggedUser.ifPresent(replyRequest::setTaggedUser);
            taggedUser.orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. id=" + replyRequest.getTaggedUserId()));
        }

        return ChildCommentDTO.ReplyResponse.builder()
                .childComment(childCommentRepository.save(replyRequest.toEntity()))
                .build();
    }

    /**
     * 대댓글 리스트 조회
     *
     * @param parentId 부모 댓글 ID
     * @return 대댓글 리스트
     */
    public List<ChildCommentDTO.ReplyResponse> getReplyList(Long parentId) {

        return commentRepositoryImpl.getReplyList(parentId);
    }

    /**
     * 댓글 삭제
     *
     * @param id 댓글 ID
     */
    @Transactional
    public void delete(Long id) {
        childCommentRepository.deleteByParentCommentId(id);
        commentRepository.deleteById(id);
    }

    /**
     * 대댓글 수정
     *
     * @param id           대댓글 ID
     * @param replyRequest 대댓글 정보
     * @return 수정된 대댓글 정보
     */
    @Transactional
    public ChildCommentDTO.ReplyResponse updateReply(Long id, ChildCommentDTO.ReplyRequest replyRequest) {
        ChildComment childComment = childCommentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + id));
        childComment.update(replyRequest.getContent());

        return ChildCommentDTO.ReplyResponse.builder()
                .childComment(childComment)
                .build();
    }

    /**
     * 대댓글 삭제
     *
     * @param id 대댓글 ID
     */
    public void deleteReply(Long id) {
        childCommentRepository.deleteById(id);
    }

    /**
     * 댓글 전체 삭제
     *
     * @param userId 사용자 ID
     */
    public void deleteAllComment(Long userId) {
        commentRepositoryImpl.deleteByUserId(userId);
    }
}
