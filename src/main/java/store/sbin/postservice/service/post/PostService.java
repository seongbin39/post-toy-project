package store.sbin.postservice.service.post;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.sbin.postservice.domain.Post;
import store.sbin.postservice.domain.User;
import store.sbin.postservice.dto.SearchCondition;
import store.sbin.postservice.dto.post.AttachmentDTO;
import store.sbin.postservice.dto.post.PostDTO;
import store.sbin.postservice.repository.UserRepository;
import store.sbin.postservice.repository.post.PostRepository;
import store.sbin.postservice.repository.post.PostRepositoryImpl;
import store.sbin.postservice.service.files.FileService;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostRepositoryImpl postRepositoryImpl;
    private final UserRepository userRepository;
    private final FileService fileService;

    public PostService(PostRepository postRepository, PostRepositoryImpl postRepositoryImpl, UserRepository userRepository, FileService fileService) {
        this.postRepository = postRepository;
        this.postRepositoryImpl = postRepositoryImpl;
        this.userRepository = userRepository;
        this.fileService = fileService;
    }

    /**
     * 게시글 저장
     *
     * @param postDto 요청된 게시글 정보
     * @param files   첨부 파일 리스트
     * @return 저장된 게시글 정보
     */
    public PostDTO.Response save(PostDTO.Request postDto, List<MultipartFile> files) {

        // 첨부 파일이 존재하는 경우, 파일 리스트를 요청된 게시글 정보에 설정
        if (files != null) {
            files.removeIf(MultipartFile::isEmpty);
            postDto.setFiles(fileService.createFileList(files));
        }
        // 작성자 정보 설정
        Optional<User> user = userRepository.findById(BigInteger.valueOf(postDto.getUserId()));
        user.ifPresent(postDto::setAuthor);
        user.orElseThrow(
                () -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. id=" + postDto.getUserId())
        );

        return PostDTO.Response.builder()
                .post(postRepository.save(postDto.toEntity()))
                .build();
    }

    /**
     * 게시글 수정
     *
     * @param id      게시글 ID
     * @param postDto 요청된 게시글 정보
     * @param files   첨부 파일 리스트
     * @throws IllegalArgumentException 해당 게시글이 존재하지 않을 경우 예외 발생
     */
    @Transactional
    public void update(Long id, PostDTO.Request postDto, List<MultipartFile> files, List<Long> deleteFiles) {

        // 게시글 조회
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        // 삭제 파일 목록이 존재하는 경우, 파일 삭제
        if (deleteFiles != null) {
            post.getFileList().stream()
                    .filter(f -> deleteFiles.contains(f.getId()))
                    .forEach(fileService::deleteFile);
            post.getFileList().removeIf(f -> deleteFiles.contains(f.getId()));
        }
        // 새로운 첨부파일이 존재하는 경우, 파일 추가
        if (files != null) {
            // 빈 파일 제거
            files.removeIf(MultipartFile::isEmpty);
            post.getFileList().addAll(fileService.createFileList(files));
        }
        // 게시글 수정
        post.update(postDto.getSubject(), postDto.getContent());
    }

    /**
     * 게시글 목록 조회
     *
     * @param pageable        페이징 정보
     * @param searchCondition 검색 조건
     * @return 게시글 목록
     */
    public Page<PostDTO.Response> getPostList(Pageable pageable, SearchCondition searchCondition) {
        return postRepositoryImpl.findPostListByCond(pageable, searchCondition);
    }

    /**
     * 최근 게시글 목록 조회
     *
     * @return 최근 게시글 목록
     */
    public List<PostDTO.Response> getRecentPostList() {
        return postRepositoryImpl.getRecentPostList();
    }

    /**
     * 공지사항 목록 조회 (최근 5개)
     *
     * @return 공지사항 목록
     */
    public List<PostDTO.Response> getNoticeList() {
        return postRepositoryImpl.findNoticeLimit5();
    }


    /**
     * 공지사항 목록 조회 (페이징)
     *
     * @param pageable        페이징
     * @param searchCondition 검색 조건
     * @return 공지사항 목록 페이지
     */
    public Page<PostDTO.Response> getNoticeListPage(Pageable pageable, SearchCondition searchCondition) {
        return postRepositoryImpl.findAllNoticePage(pageable, searchCondition);
    }


    /**
     * 게시글 상세 조회
     *
     * @param id 게시글 ID
     * @return 게시글 상세 정보
     */
    public PostDTO.Response getPostDetail(Long id) {
        return postRepositoryImpl.findPostById(id);
    }

    /**
     * 게시글 삭제
     *
     * @param id 게시글 ID
     */
    @Transactional
    public void delete(Long id) {
        postRepository.deleteById(id);
        for (AttachmentDTO attachmentDTO : getAttachmentList(id)) {
            fileService.deleteFile(attachmentDTO.getFileUrl());
        }
    }

    /**
     * 게시글 조회수 증가
     *
     * @param id 게시글 ID
     */
    public void updateReadCount(Long id) {
        postRepositoryImpl.updateReadCount(id);
    }

    /**
     * 게시글 첨부파일 목록 조회
     *
     * @param id 게시글 ID
     * @return 첨부파일 목록
     */
    public List<AttachmentDTO> getAttachmentList(Long id) {
        return postRepositoryImpl.findPostAttachListById(id);
    }

    /**
     * 사용자 게시글 모두 삭제
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void deleteAllPost(Long userId) {
        postRepositoryImpl.deleteAllPost(userId);
    }

}
