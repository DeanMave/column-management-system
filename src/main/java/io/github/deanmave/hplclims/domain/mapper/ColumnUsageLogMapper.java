package io.github.deanmave.hplclims.domain.mapper;

import io.github.deanmave.hplclims.domain.ColumnUsageLog;
import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.dto.request.CorrectUsageLogRequest;
import io.github.deanmave.hplclims.domain.dto.request.StartUsageRequest;
import io.github.deanmave.hplclims.domain.dto.response.UsageLogResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class, HplcColumnMapper.class})
public interface ColumnUsageLogMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startDate", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    ColumnUsageLog toColumnUsageLog(StartUsageRequest request, User user, HplcColumn hplcColumn);

    UsageLogResponseDto toUsageLogResponseDto(ColumnUsageLog columnUsageLog);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "hplcColumn", ignore = true)
    @Mapping(target = "endDate", ignore = true)
    ColumnUsageLog updateFromDto(@MappingTarget ColumnUsageLog columnUsageLog, CorrectUsageLogRequest dto);
}
