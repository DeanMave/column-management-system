package io.github.deanmave.hplclims.domain.mapper;

import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.domain.dto.request.ColumnCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.ColumnUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.ColumnResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface HplcColumnMapper {
    ColumnResponseDto toColumnResponseDto(HplcColumn hplcColumn);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "internalCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "returnDate",ignore = true)
    HplcColumn toHplcColumn(ColumnCreateDto dto);

    @Mapping(target = "id",ignore = true)
    HplcColumn updateFromDto(@MappingTarget HplcColumn hplcColumn, ColumnUpdateDto dto);
}
