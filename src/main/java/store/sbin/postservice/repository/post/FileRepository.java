package store.sbin.postservice.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import store.sbin.postservice.domain.Attachment;

import java.util.Optional;

public interface FileRepository extends JpaRepository<Attachment, Long> {

    Optional<Attachment> findByFileName(String fileName);
}
