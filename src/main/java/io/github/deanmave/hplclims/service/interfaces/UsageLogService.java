package io.github.deanmave.hplclims.service.interfaces;

import io.github.deanmave.hplclims.domain.dto.request.EndUsageRequest;
import io.github.deanmave.hplclims.domain.dto.request.RejectUsageRequest;
import io.github.deanmave.hplclims.domain.dto.request.StartUsageRequest;
import io.github.deanmave.hplclims.domain.dto.request.CorrectUsageLogRequest;
import io.github.deanmave.hplclims.domain.dto.response.UsageLogResponseDto;

import java.util.List;

public interface UsageLogService {
    UsageLogResponseDto startUsage(Long userId, Long hplcColumnId, StartUsageRequest request);

    UsageLogResponseDto endUsage(Long logId, EndUsageRequest request);

    UsageLogResponseDto rejectUsage(Long logId, RejectUsageRequest request);

    UsageLogResponseDto correctLog(Long logId, CorrectUsageLogRequest request);

    List<UsageLogResponseDto> getLogsByColumn(Long hplcColumnId);

    List<UsageLogResponseDto> getLogsByUser(Long userId);

    List<UsageLogResponseDto> getActiveUsages();

    void deleteById(Long logId);
}
