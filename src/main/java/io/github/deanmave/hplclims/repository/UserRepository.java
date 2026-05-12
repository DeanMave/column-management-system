package io.github.deanmave.hplclims.repository;

import io.github.deanmave.hplclims.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
