package store.sbin.postservice.repository.post;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import store.sbin.postservice.domain.QUser;
import store.sbin.postservice.dto.UserDTO.UserResponse;
import store.sbin.postservice.dto.post.ChildCommentDTO;
import store.sbin.postservice.dto.post.CommentDTO;

import java.util.List;

import static store.sbin.postservice.domain.QChildComment.childComment;
import static store.sbin.postservice.domain.QComments.comments;
import static store.sbin.postservice.domain.QUser.user;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CommentRepositoryImpl {

    private final JPAQueryFactory queryFactory;

    /**
     * 게시글 ID로 댓글 목록 조회
     * @param postId 게시글 ID
     * @return 댓글 목록
     */
    @EntityGraph(attributePaths = "children")
    public List<CommentDTO.CommentResponse> findAllByPostId(Long postId) {

        return queryFactory
                .select(Projections.fields(CommentDTO.CommentResponse.class,
                        comments.id
                        , comments.postId
                        , comments.content
                        , comments.createdDate
                        , comments.modifiedDate
                        , Expressions.as(JPAExpressions.select(childComment.count())
                                .from(childComment)
                                .where(childComment.parentCommentId.eq(comments.id))
                                , "replyCount")
                        // 작성자 정보
                        , Expressions.as(Projections.fields(UserResponse.class,
                                user.id
                                , user.username
                                , user.email
                                , user.role
                                , user.picture
                        ), "author")
                ))
                .from(comments)
                .innerJoin(user)
                    .on(comments.author.id.eq(user.id))
                .where(comments.postId.eq(postId))
                .fetch();
    }

    /**
     * 댓글 ID로 답글 조회
     * @return 댓글
     */
    public List<ChildCommentDTO.ReplyResponse> getReplyList(Long parentCommentId) {
        QUser authorUser = new QUser("authorUser");
        QUser taggedUser = new QUser("taggedUser");

        return queryFactory
                .select(Projections.fields(ChildCommentDTO.ReplyResponse.class,
                        childComment.id
                        , childComment.parentCommentId
                        , childComment.content
                        , childComment.createdDate
                        , childComment.modifiedDate
                        , Expressions.as(Projections.fields(UserResponse.class,
                                authorUser.id
                                , authorUser.username
                                , authorUser.email
                                , authorUser.role
                                , authorUser.picture
                        ), "author")
                        , Expressions.as(Projections.fields(UserResponse.class,
                                taggedUser.id
                                , taggedUser.username
                                , taggedUser.email
                                , taggedUser.picture
                        ), "taggedUser")
                ))
                .from(childComment)
                .innerJoin(authorUser)
                .on(childComment.author.id.eq(authorUser.id))
                .leftJoin(taggedUser)
                .on(childComment.taggedUser.id.eq(taggedUser.id))
                .where(childComment.parentCommentId.eq(parentCommentId))
                .orderBy(childComment.createdDate.asc())
                .fetch();
    }

    public void deleteByUserId(Long userId) {
        queryFactory
                .delete(comments)
                .where(comments.author.id.eq(userId))
                .execute();

        queryFactory
                .delete(childComment)
                .where(childComment.author.id.eq(userId))
                .execute();
    }
}
