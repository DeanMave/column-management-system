package io.github.deanmave.hplclims.service.interfaces;

import io.github.deanmave.hplclims.domain.ColumnUsageLog;
import io.github.deanmave.hplclims.domain.dto.request.EndUsageRequest;
import io.github.deanmave.hplclims.domain.dto.request.StartUsageRequest;
import io.github.deanmave.hplclims.domain.dto.request.CorrectUsageLogRequest;

import java.time.LocalDate;
import java.util.List;

public interface UsageLogService {
    ColumnUsageLog startUsage(Long userId, Long hplcColumnId, StartUsageRequest request);

    ColumnUsageLog endUsage(Long logId, EndUsageRequest request);

    ColumnUsageLog rejectUsage(Long logId, String reason, LocalDate rejectionDate);

    ColumnUsageLog correctLog(Long logId, CorrectUsageRequest request);

    List<ColumnUsageLog> getLogsByColumn(Long hplcColumnId);

    List<ColumnUsageLog> getLogsByUser(Long userId);

    List<ColumnUsageLog> getActiveUsages();
}
