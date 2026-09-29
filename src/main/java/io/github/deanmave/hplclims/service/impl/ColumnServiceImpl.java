package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.domain.dto.request.ColumnCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.ColumnUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.ColumnResponseDto;
import io.github.deanmave.hplclims.domain.mapper.HplcColumnMapper;
import io.github.deanmave.hplclims.exception.NotFoundException;
import io.github.deanmave.hplclims.repository.ColumnRepository;
import io.github.deanmave.hplclims.service.interfaces.ColumnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ColumnServiceImpl implements ColumnService {
    private static final String INT_PREFIX = "INT";
    private static final String EXT_PREFIX = "EXT";

    private final ColumnRepository repository;
    private final HplcColumnMapper mapper;


    @Override
    @Transactional
    public ColumnResponseDto create(ColumnCreateDto createDto) {
        log.info("Попытка добавления новой колонки Администратором: {}", createDto);
        HplcColumn createColumn = mapper.toHplcColumn(createDto);
        createColumn.setInternalCode(generateInternalCode(createColumn));
        createColumn.setStatus(ColumnStatus.AVAILABLE);
        HplcColumn savedColumn = repository.save(createColumn);
        log.info("Колонка добавлена: {}", savedColumn);
        return mapper.toColumnResponseDto(savedColumn);
    }

    @Override
    public List<ColumnResponseDto> getAll() {
        log.info("Запрос на получение всех колонок");
        return repository.findAll().stream()
                .map(mapper::toColumnResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public ColumnResponseDto getById(Long id) {
        log.info("Запрос поиска колонки по ID: {}", id);
        return mapper.toColumnResponseDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Колонка с ID " + id + " не найдена")));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        log.info("Попытка удаления колонки по ID: {}", id);
        if (!repository.existsById(id)) {
            throw new NotFoundException("Колонка с ID " + id + " не найдена");
        }
        repository.deleteById(id);
        log.info("Колонка с ID {} удалена", id);
    }

    @Override
    @Transactional
    public ColumnResponseDto changeStatus(Long id, ColumnStatus newStatus) {
        log.info("Попытка обновления колонки с ID: {}", id);
        HplcColumn existingColumn = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Колонка с ID: " + id + " не найдена"));
        existingColumn.setStatus(newStatus);
        HplcColumn updatedColumn = repository.save(existingColumn);
        log.info("Колонка обновлена: {}", updatedColumn);
        return mapper.toColumnResponseDto(updatedColumn);
    }

    @Override
    @Transactional
    public ColumnResponseDto correctData(Long id, ColumnUpdateDto updateDto) {
        log.info("Попытка обновления колонки с ID: {}", id);
        HplcColumn existingColumn = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Колонка с ID: " + id + " не найдена"));
        HplcColumn updatedColumn = repository.save(mapper.updateFromDto(existingColumn,updateDto));
        log.info("Колонка обновлена: {}", updatedColumn);
        return mapper.toColumnResponseDto(updatedColumn);
    }

    private String generateInternalCode(HplcColumn createColumn) {
        int currentYear = createColumn.getInstallationDate().getYear();
        if (createColumn.isExternal()) {
            Long sequenceValue = repository.getNextExtSequenceValue();
            return String.format("%s-%d-%03d", EXT_PREFIX, currentYear, sequenceValue);
        }
        Long sequenceValue = repository.getNextIntSequenceValue();
        return String.format("%s-%d-%03d", INT_PREFIX, currentYear, sequenceValue);
    }
}
