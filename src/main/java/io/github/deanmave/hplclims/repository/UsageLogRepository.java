package io.github.deanmave.hplclims.repository;

import io.github.deanmave.hplclims.domain.ColumnUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsageLogRepository extends JpaRepository<ColumnUsageLog,Long> {
}
