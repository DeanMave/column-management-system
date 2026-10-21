package io.github.deanmave.hplclims.repository;

import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.UserPosition;
import io.github.deanmave.hplclims.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByLogin(String login);

    @Query(value = """ 
            SELECT u FROM User u
            WHERE (:isActive IS NULL OR u.isActive = :isActive)
            AND (:role IS NULL OR u.role = :role)
            AND (:position IS NULL OR u.position = :position)
            """)
    List<User> findFiltered(Boolean isActive, UserRole role, UserPosition position);

    Optional<User> findByLogin(String login);
}
