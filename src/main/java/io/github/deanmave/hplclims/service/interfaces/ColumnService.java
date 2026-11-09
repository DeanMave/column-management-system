package io.github.deanmave.hplclims.service.interfaces;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.domain.dto.request.ColumnCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.ColumnUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.ColumnResponseDto;

import java.util.List;

public interface ColumnService {
    ColumnResponseDto create(ColumnCreateDto createDto);

    List<ColumnResponseDto> getColumnsFiltered(Boolean isExternal, String ownerOrganization,
                                               String internalCode, String storageLocation,
                                               ColumnStatus status);

    ColumnResponseDto getById(Long id);

    void deleteById(Long id);

    ColumnResponseDto changeStatus(Long id, ColumnStatus newStatus);

    ColumnResponseDto correctData(Long id, ColumnUpdateDto updateDto);
}
