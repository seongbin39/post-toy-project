package store.sbin.postservice.repository.post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.sbin.postservice.domain.Comments;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long>{

    @EntityGraph(attributePaths = {"author"}, type = EntityGraph.EntityGraphType.LOAD)
    List<Comments> findAllByPostId(Long postId);

//    List<Comments> findAllByParentId(Long parentId);
}
