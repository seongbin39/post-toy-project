package store.sbin.postservice.repository.post;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import store.sbin.postservice.dto.SearchCondition;
import store.sbin.postservice.dto.UserDTO.UserResponse;
import store.sbin.postservice.dto.post.AttachmentDTO;
import store.sbin.postservice.dto.post.PostDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static store.sbin.postservice.domain.QAttachment.attachment;
import static store.sbin.postservice.domain.QPost.post;
import static store.sbin.postservice.domain.QUser.user;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    /**
     * getPostById : 게시글 상세 조회
     *
     * @param id 게시글 ID
     * @return PostDTO 게시글 상세 정보
     */
    @Transactional
    public PostDTO.Response findPostById(Long id) {
        return queryFactory
                .select(Projections.fields(PostDTO.Response.class,
                        post.id
                        , post.subject
                        , post.content
                        , post.createdDate
                        , post.modifiedDate
                        , post.readCount
                        , Expressions.as(Projections.fields(UserResponse.class,
                                user.id
                                , user.username
                                , user.email
                                , user.role
                                , user.picture
                        ), "author")
                ))
                .from(post)
                .innerJoin(user)
                .on(post.writer.id.eq(user.id))
                .where(post.id.eq(id))
                .fetchOne();
    }

    /**
     * getPostAttachById : 게시글 첨부파일 조회
     *
     * @param id 게시글 ID
     * @return List<AttachmentDTO> 첨부파일 목록
     */
    public List<AttachmentDTO> findPostAttachListById(Long id) {
        return queryFactory
                .select(Projections.fields(AttachmentDTO.class,
                        post.id
                        , attachment.id
                        , attachment.fileName
                        , attachment.fileOriName
                        , attachment.fileUrl
                        , attachment.fileType
                        , attachment.fileSize

                ))
                .from(post)
                .innerJoin(attachment)
                .on(post.id.eq(attachment.postId))
                .where(post.id.eq(id))
                .fetch();
    }

    /**
     * updateReadCount : 조회수 증가
     *
     * @param id 게시글 ID
     */
    @Transactional
    public void updateReadCount(Long id) {
        queryFactory
                .update(post)
                .set(post.readCount, post.readCount.add(1))
                .where(post.id.eq(id))
                .execute();
    }

    /**
     * getPostList : 검색 조건에 따른 게시글 목록 조회
     *
     * @param pageable        페이징 정보
     * @param searchCondition 검색 조건
     * @return Page<PostDTO>
     */
    public Page<PostDTO.Response> findPostListByCond(Pageable pageable, SearchCondition searchCondition) {

        // 게시글 수 조회
        JPAQuery<Long> count = queryFactory
                .select(post.count())
                .from(post)
                .where(
                        containsKeyword(searchCondition),
                        searchPeriod(searchCondition)
                );

        List<PostDTO.Response> result = queryFactory
                .select(Projections.fields(PostDTO.Response.class,
                        post.id
                        , post.subject
                        , post.content
                        , post.createdDate
                        , post.modifiedDate
                        , post.readCount
                        , post.notice
                        , Expressions.as(Projections.fields(UserResponse.class,
                                user.id
                                , user.username
                                , user.email
                                , user.role
                                , user.picture
                        ), "author")
                        , Expressions.as(JPAExpressions.select(attachment.count())
                                .from(attachment)
                                .where(attachment.postId.eq(post.id)), "attachCount") // 첨부파일 수
                ))
                .from(post)
                .innerJoin(user)
                .on(post.writer.id.eq(user.id))
                .where(
                        containsKeyword(searchCondition),
                        searchPeriod(searchCondition)
                )
                .orderBy(getOrderSpecifier(pageable.getSort()).toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    /**
     * getRecentPostList : 최근 게시글 목록 조회
     *
     * @return List<PostDTO> 최근 게시글 목록
     */
    public List<PostDTO.Response> getRecentPostList() {
        List<PostDTO.Response> result = queryFactory
                .select(Projections.fields(PostDTO.Response.class,
                        post.id
                        , post.subject
                        , post.content
                        , post.createdDate
                        , post.modifiedDate
                        , post.readCount
                        , Expressions.as(Projections.fields(UserResponse.class,
                                user.id
                                , user.username
                                , user.email
                                , user.role
                                , user.picture
                        ), "author")
                        , Expressions.as(JPAExpressions.select(attachment.count())
                                .from(attachment)
                                .where(attachment.postId.eq(post.id)), "attachCount") // 첨부파일 수
                ))
                .from(post)
                .innerJoin(user)
                .on(post.writer.id.eq(user.id))
                .where(post.notice.isFalse())
                .orderBy(post.createdDate.desc())
                .limit(5)
                .fetch();

        return result;
    }

    /**
     * getNoticeListPage : 공지사항 목록 조회
     *
     * @param pageable        페이징 정보
     * @param searchCondition 검색 조건
     * @return Page<PostDTO> 공지사항 목록
     */
    public Page<PostDTO.Response> findAllNoticePage(Pageable pageable, SearchCondition searchCondition) {
        JPAQuery<Long> count = queryFactory
                .select(post.count())
                .from(post)
                .where(post.notice.isTrue());

        List<PostDTO.Response> result = queryFactory
                .select(Projections.fields(PostDTO.Response.class,
                        post.id
                        , post.subject
                        , post.content
                        , post.createdDate
                        , post.modifiedDate
                        , post.readCount
                        , post.notice
                        , Expressions.as(Projections.fields(UserResponse.class,
                                user.id
                                , user.username
                                , user.email
                                , user.role
                                , user.picture
                        ), "author")
                        , Expressions.as(JPAExpressions.select(attachment.count())
                                .from(attachment)
                                .where(attachment.postId.eq(post.id)), "attachCount") // 첨부파일 수
                ))
                .from(post)
                .innerJoin(user)
                .on(post.writer.id.eq(user.id))
                .where(
                        post.notice.isTrue(),
                        containsKeyword(searchCondition),
                        searchPeriod(searchCondition)
                )
                .orderBy(post.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return PageableExecutionUtils.getPage(result, pageable, count::fetchOne);
    }

    /**
     * getNoticeList : 공지사항 목록 조회
     *
     * @return List<PostDTO> 공지사항 목록 최신순 5개
     */
    public List<PostDTO.Response> findNoticeLimit5() {
        List<PostDTO.Response> result = queryFactory
                .select(Projections.fields(PostDTO.Response.class,
                        post.id
                        , post.subject
                        , post.content
                        , post.createdDate
                        , post.modifiedDate
                        , post.readCount
                        , Expressions.as(Projections.fields(UserResponse.class,
                                user.id
                                , user.username
                                , user.email
                                , user.role
                                , user.picture
                        ), "author")
                        , Expressions.as(JPAExpressions.select(attachment.count())
                                .from(attachment)
                                .where(attachment.postId.eq(post.id)), "attachCount") // 첨부파일 수
                ))
                .from(post)
                .innerJoin(user)
                .on(post.writer.id.eq(user.id))
                .where(post.notice.isTrue())
                .orderBy(post.createdDate.desc())
                .limit(5)
                .fetch();

        return result;
    }

    /**
     * getOrderSpecifier : 동적으로 정렬하기 위해서 적용
     *
     * @param sort 정렬 정보
     * @return OrderSpecifier
     */
    private List<OrderSpecifier<?>> getOrderSpecifier(Sort sort) {
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        orders.add(new OrderSpecifier(Order.DESC, post.notice));

        sort.forEach(order -> {
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            String prop = order.getProperty();
            Path<Object> path = Expressions.path(Object.class, post, prop);
            orders.add(new OrderSpecifier(direction, path));
        });
        return orders;
    }

    /**
     * searchPeriod : 검색 기간에 따른 조건 생성
     *
     * @param searchCondition 검색 조건
     * @return BooleanExpression
     */
    private BooleanExpression searchPeriod(SearchCondition searchCondition) {
        String searchPeriod = searchCondition.getSearchPeriod();
        if (searchPeriod == null || searchPeriod.isBlank()) {
            return null;
        }
        LocalDate now = LocalDate.now();
        return switch (searchPeriod) {
            case "today" -> post.createdDate.after(now.atStartOfDay());
            case "week" -> post.createdDate.after(now.atStartOfDay().minusWeeks(1));
            case "month" -> post.createdDate.after(now.atStartOfDay().minusMonths(1));
            default -> null;
        };
    }

    /**
     * containsKeyword : 검색어에 따른 조건 생성
     *
     * @param searchCondition 검색 조건
     * @return BooleanExpression
     */
    private BooleanExpression containsKeyword(SearchCondition searchCondition) {
        String keyword = searchCondition.getKeyword();
        String searchType = searchCondition.getSearchType();
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        return switch (searchType) {
            case "subject" -> post.subject.containsIgnoreCase(keyword);
            case "content" -> post.content.containsIgnoreCase(keyword);
            case "author" -> post.writer.username.containsIgnoreCase(keyword);
            default -> post.subject.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword));
        };
    }

    /**
     * deleteAllPost : 사용자 전체 게시글, 첨부파일 삭제
     *
     * @param userId 사용자 ID
     */
    public void deleteAllPost(Long userId) {
        // 첨부 파일 삭제
        queryFactory.delete(attachment)
                .where(attachment.postId.in(
                        JPAExpressions.select(post.id)
                                .from(post)
                                .where(post.writer.id.eq(userId))
                ))
                .execute();

        // 게시글 삭제
        queryFactory.delete(post)
                .where(post.writer.id.eq(userId))
                .execute();
    }
}
