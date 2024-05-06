package store.sbin.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.sbin.postservice.domain.User;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, BigInteger> {
    Optional<User> findByEmail(String email);
    Optional<User> findBySocialId(String socialId);
}
