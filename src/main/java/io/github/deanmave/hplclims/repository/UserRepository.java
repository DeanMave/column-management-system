package io.github.deanmave.hplclims.repository;

import io.github.deanmave.hplclims.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsByLogin(String login);

    List<User> findByIsActive(boolean isActive);
}
