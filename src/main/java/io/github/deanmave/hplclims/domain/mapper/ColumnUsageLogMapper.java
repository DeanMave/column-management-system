package io.github.deanmave.hplclims.domain.mapper;

import io.github.deanmave.hplclims.domain.ColumnUsageLog;
import io.github.deanmave.hplclims.domain.dto.request.CorrectUsageLogRequest;
import io.github.deanmave.hplclims.domain.dto.response.UsageLogResponseDto;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UserMapper.class, HplcColumnMapper.class})
public interface ColumnUsageLogMapper {
    UsageLogResponseDto toUsageLogResponseDto(ColumnUsageLog columnUsageLog);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "hplcColumn", ignore = true)
    ColumnUsageLog updateFromDto(@MappingTarget ColumnUsageLog columnUsageLog, CorrectUsageLogRequest dto);

    List<UsageLogResponseDto> toDtoList(List<ColumnUsageLog> logs);
}
