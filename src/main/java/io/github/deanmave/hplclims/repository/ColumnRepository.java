package io.github.deanmave.hplclims.repository;

import io.github.deanmave.hplclims.domain.HplcColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ColumnRepository extends JpaRepository<HplcColumn, Long> {
    @Query(value = "SELECT nextval('column_int_code_seq')", nativeQuery = true)
    Long getNextIntSequenceValue();

    @Query(value = "SELECT nextval('column_ext_code_seq')", nativeQuery = true)
    Long getNextExtSequenceValue();
}
