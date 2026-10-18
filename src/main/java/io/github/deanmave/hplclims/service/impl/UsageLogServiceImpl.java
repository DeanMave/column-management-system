package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.ColumnUsageLog;
import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.dto.request.CorrectUsageLogRequest;
import io.github.deanmave.hplclims.domain.dto.request.EndUsageRequest;
import io.github.deanmave.hplclims.domain.dto.request.RejectUsageRequest;
import io.github.deanmave.hplclims.domain.dto.request.StartUsageRequest;
import io.github.deanmave.hplclims.domain.dto.response.UsageLogResponseDto;
import io.github.deanmave.hplclims.domain.mapper.ColumnUsageLogMapper;
import io.github.deanmave.hplclims.exception.ConflictException;
import io.github.deanmave.hplclims.exception.NotFoundException;
import io.github.deanmave.hplclims.exception.ValidationException;
import io.github.deanmave.hplclims.repository.ColumnRepository;
import io.github.deanmave.hplclims.repository.UsageLogRepository;
import io.github.deanmave.hplclims.repository.UserRepository;
import io.github.deanmave.hplclims.service.interfaces.ColumnService;
import io.github.deanmave.hplclims.service.interfaces.UsageLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsageLogServiceImpl implements UsageLogService {
    private final UsageLogRepository repository;
    private final ColumnService columnService;
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final ColumnUsageLogMapper mapper;

    @Override
    @Transactional
    public UsageLogResponseDto startUsage(StartUsageRequest request) {
        log.info("Попытка взять пользователем:{} колонку:{} в работу", request.getUserId(), request.getColumnId());
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + request.getUserId() + " не найден"));
        if (!user.isActive()) {
            throw new ValidationException("Только действующие сотрудники могут брать колонку в работу");
        }
        HplcColumn hplcColumn = columnRepository.findById(request.getColumnId())
                .orElseThrow(() -> new NotFoundException("Колонка с ID " + request.getColumnId() + " не найден"));
        if (!hplcColumn.isAvailable()) {
            throw new ConflictException("Колонка с id: " + hplcColumn.getId() + " сейчас занята");
        }
        columnService.changeStatus(hplcColumn.getId(), ColumnStatus.IN_USE);
        ColumnUsageLog newLog = mapper.toColumnUsageLog(request, user, hplcColumn);
        newLog.setStartDate(LocalDate.now());
        ColumnUsageLog savedLog = repository.save(newLog);
        log.info("Лог добавлен:{}", savedLog.getId());
        return mapper.toUsageLogResponseDto(savedLog);
    }

    @Override
    @Transactional
    public UsageLogResponseDto endUsage(Long logId, EndUsageRequest request) {
        log.info("Попытка завершения анализа для лога:{}", logId);
        ColumnUsageLog existLog = repository.findById(logId)
                .orElseThrow(() -> new NotFoundException("Лога с id: " + logId + " не найдено"));
        if (existLog.getEndDate() != null) {
            throw new ConflictException("Анализ по логу с id: " + logId + " уже завершён");
        }
        if (request.getEndDate().isBefore(existLog.getStartDate()) || request.getEndDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата завершения анализа не может быть раньше начальной или позже сегодняшней даты");
        }
        existLog.setAnalysisParameters(request.getAnalysisParameters());
        existLog.setStoragePhase(request.getStoragePhase());
        existLog.setMinPressure(request.getMinPressure());
        existLog.setMaxPressure(request.getMaxPressure());
        existLog.setEndDate(request.getEndDate());
        columnService.changeStatus(existLog.getHplcColumn().getId(), ColumnStatus.AVAILABLE);
        ColumnUsageLog endLog = repository.save(existLog);
        log.info("Лог успешно завершен:{}", endLog.getId());
        return mapper.toUsageLogResponseDto(endLog);
    }

    @Override
    @Transactional
    public UsageLogResponseDto rejectUsage(Long logId, RejectUsageRequest request) {
        log.info("Попытка отказа от колонки для лога:{}", logId);
        ColumnUsageLog existLog = repository.findById(logId)
                .orElseThrow(() -> new NotFoundException("Лога с id: " + logId + " не найдено"));
        if (existLog.getEndDate() != null) {
            throw new ConflictException("Анализ по логу с id: " + logId + " уже завершён");
        }
        if (request.getDate().isBefore(existLog.getStartDate()) || request.getDate().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата отказа не может быть раньше начальной или позже сегодняшней даты");
        }
        existLog.setEndDate(request.getDate());
        existLog.setRejectionReason(request.getReason());
        columnService.changeStatus(existLog.getHplcColumn().getId(), ColumnStatus.AVAILABLE);
        ColumnUsageLog savedLog = repository.save(existLog);
        log.info("Отказ от колонки завершен, лог успешно закрыт:{}", savedLog.getId());
        return mapper.toUsageLogResponseDto(savedLog);
    }

    @Override
    @Transactional
    public UsageLogResponseDto correctLog(Long logId, CorrectUsageLogRequest request) {
        log.info("Попытка изменения существующего лога с ID: {}", logId);
        ColumnUsageLog existLog = repository.findById(logId)
                .orElseThrow(() -> new NotFoundException("Лога с id: " + logId + " не найдено"));
        if (request.getEndDate() != null) {
            if (request.getEndDate().isBefore(existLog.getStartDate()) || request.getEndDate().isAfter(LocalDate.now())) {
                throw new ValidationException("Дата завершения не может быть раньше начальной или позже сегодняшней даты");
            }
            boolean wasActive = (existLog.getEndDate() == null);
            existLog.setEndDate(request.getEndDate());
            if (wasActive) {
                columnService.changeStatus(existLog.getHplcColumn().getId(), ColumnStatus.AVAILABLE);
            }
        }
        ColumnUsageLog updatedLog = mapper.updateFromDto(existLog, request);
        ColumnUsageLog endLog = repository.save(updatedLog);
        log.info("Лог успешно изменён:{}", endLog.getId());
        return mapper.toUsageLogResponseDto(endLog);
    }

    @Override
    public List<UsageLogResponseDto> getLogsByColumn(Long hplcColumnId) {
        log.info("Запрос на получение логов для колонки с id:{}", hplcColumnId);
        columnService.getById(hplcColumnId);
        return repository.findByHplcColumn_Id(hplcColumnId).stream()
                .map(mapper::toUsageLogResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsageLogResponseDto> getLogsByUser(Long userId) {
        log.info("Запрос на получение логов для пользователя с id:{}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        return repository.findByUser_Id(userId).stream()
                .map(mapper::toUsageLogResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsageLogResponseDto> getActiveUsages() {
        log.info("Запрос на получение незавершенных логов");
        return repository.findByEndDateIsNull().stream()
                .map(mapper::toUsageLogResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long logId) {
        log.info("Попытка удаления лога с id:{}", logId);
        repository.findById(logId)
                .orElseThrow(() -> new NotFoundException("Лог c ID " + logId + " не найден"));
        repository.deleteById(logId);
    }
}
