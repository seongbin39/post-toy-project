package store.sbin.postservice.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.sbin.postservice.domain.Post;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Override
//    @EntityGraph(attributePaths = {"fileList", "writer", "comments"}, type = EntityGraph.EntityGraphType.LOAD)
    Optional<Post> findById(Long id);
}
