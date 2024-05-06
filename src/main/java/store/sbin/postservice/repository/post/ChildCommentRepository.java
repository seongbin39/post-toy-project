package store.sbin.postservice.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import store.sbin.postservice.domain.ChildComment;

public interface ChildCommentRepository extends JpaRepository<ChildComment, Long> {
    void deleteByParentCommentId(Long id);
}
