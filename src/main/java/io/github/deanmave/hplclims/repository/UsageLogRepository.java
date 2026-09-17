package io.github.deanmave.hplclims.repository;

import io.github.deanmave.hplclims.domain.ColumnUsageLog;
import io.github.deanmave.hplclims.domain.HplcColumn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsageLogRepository extends JpaRepository<ColumnUsageLog,Long> {
    List<ColumnUsageLog> findByHplcColumn_Id(Long columnId);

    List<ColumnUsageLog> findByUser_Id(Long userId);

    List<ColumnUsageLog> findByEndDateIsNull();
}
