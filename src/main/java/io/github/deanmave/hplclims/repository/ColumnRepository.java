package io.github.deanmave.hplclims.repository;

import io.github.deanmave.hplclims.domain.HplcColumn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColumnRepository extends JpaRepository<HplcColumn,Long> {
}
