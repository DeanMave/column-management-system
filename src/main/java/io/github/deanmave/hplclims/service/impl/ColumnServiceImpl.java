package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.exception.ConflictException;
import io.github.deanmave.hplclims.exception.NotFoundException;
import io.github.deanmave.hplclims.repository.ColumnRepository;
import io.github.deanmave.hplclims.service.interfaces.ColumnService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ColumnServiceImpl implements ColumnService {
    private final ColumnRepository repository;

    @Override
    @Transactional
    public HplcColumn create(HplcColumn hplcColumn) {
        log.info("Попытка добавления новой колонки: {}", hplcColumn);
        if (repository.existsByInternalCode(hplcColumn.getInternalCode())) {
            throw new ConflictException("Колонка с internalCode " + hplcColumn.getInternalCode() + " уже существует.");
        }
        hplcColumn.setStatus(ColumnStatus.AVAILABLE);
        HplcColumn savedColumn = repository.save(hplcColumn);
        log.info("Колонка добавлена: {}", savedColumn);
        return savedColumn;
    }

    @Override
    public List<HplcColumn> getAll() {
        log.info("Запрос на получение всех колонок");
        return repository.findAll();
    }

    @Override
    public HplcColumn getById(Long id) {
        log.info("Запрос поиска колонки по ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Колонка с ID " + id + " не найдена"));
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
    public HplcColumn changeStatus(Long id, ColumnStatus newStatus) {
        log.info("Попытка обновления колонки с ID: {}", id);
        HplcColumn existingColumn = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Колонка с ID: " + id + " не найдена"));
        existingColumn.setStatus(newStatus);
        HplcColumn updatedColumn = repository.save(existingColumn);
        log.info("Колонка обновлена: {}", updatedColumn);
        return updatedColumn;
    }

    @Override
    @Transactional
    public HplcColumn correctData(Long id, HplcColumn newColumn) {
        log.info("Попытка обновления колонки с ID: {}", id);
        HplcColumn existingColumn = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Колонка с ID: " + id + " не найдена"));
        HplcColumn updatedColumn = repository.save(setData(existingColumn,newColumn));
        log.info("Колонка обновлена: {}", updatedColumn);
        return updatedColumn;
    }

    private HplcColumn setData(HplcColumn existingColumn, HplcColumn newColumn){
        existingColumn.setManufacturer(newColumn.getManufacturer());
        existingColumn.setSerialNumber(newColumn.getSerialNumber());
        existingColumn.setPartNumber(newColumn.getPartNumber());
        existingColumn.setLength(newColumn.getLength());
        existingColumn.setDiameter(newColumn.getDiameter());
        existingColumn.setParticleSize(newColumn.getParticleSize());
        existingColumn.setInstallationDate(newColumn.getInstallationDate());
        existingColumn.setPhMin(newColumn.getPhMin());
        existingColumn.setPhMax(newColumn.getPhMax());
        existingColumn.setMaxPressure(newColumn.getMaxPressure());
        existingColumn.setOwnerOrganization(newColumn.getOwnerOrganization());
        existingColumn.setReturnDate(newColumn.getReturnDate());
        if (!existingColumn.getInternalCode().equals(newColumn.getInternalCode())
            && repository.existsByInternalCode(newColumn.getInternalCode())) {
            throw new ConflictException("internalCode уже занят: " + newColumn.getInternalCode());
        }
        existingColumn.setInternalCode(newColumn.getInternalCode());
        return existingColumn;
    }
}
