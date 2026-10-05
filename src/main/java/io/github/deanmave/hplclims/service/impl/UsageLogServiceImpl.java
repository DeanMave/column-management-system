package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.ColumnUsageLog;
import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.dto.request.CorrectUsageLogRequest;
import io.github.deanmave.hplclims.domain.dto.request.EndUsageRequest;
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
import org.springframework.util.StringUtils;

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
    public UsageLogResponseDto startUsage(Long userId, Long hplcColumnId, StartUsageRequest request) {
        log.info("Попытка взять пользователем:{} колонку:{} в работу", userId, hplcColumnId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
        if (!user.isActive()) {
            throw new ValidationException("Только действующие сотрудники могут брать колонку в работу");
        }
        HplcColumn hplcColumn = columnRepository.findById(hplcColumnId)
                .orElseThrow(() -> new NotFoundException("Колонка с ID " + hplcColumnId + " не найден"));
        if (!hplcColumn.isAvailable()) {
            throw new ConflictException("Колонка с id: " + hplcColumnId + " сейчас занята");
        }
        if (!StringUtils.hasText(request.getTaskNumber()) || !StringUtils.hasText(request.getDrugName())) {
            throw new ValidationException("Поля с номером задания и наименованием препарата должны быть заполнены");
        }
        columnService.changeStatus(hplcColumnId, ColumnStatus.IN_USE);
        ColumnUsageLog newLog = mapper.toColumnUsageLog(request,user,hplcColumn);
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
        if (!StringUtils.hasText(request.getAnalysisParameters()) || !StringUtils.hasText(request.getStoragePhase())
            || request.getMinPressure() == null || request.getMaxPressure() == null || request.getEndDate() == null) {
            throw new ValidationException("Поля связанные с завершением анализа должны быть заполнены");
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
    public UsageLogResponseDto rejectUsage(Long logId, String reason, LocalDate rejectionDate) {
        log.info("Попытка отказа от колонки для лога:{}", logId);
        ColumnUsageLog existLog = repository.findById(logId)
                .orElseThrow(() -> new NotFoundException("Лога с id: " + logId + " не найдено"));
        if (existLog.getEndDate() != null) {
            throw new ConflictException("Анализ по логу с id: " + logId + " уже завершён");
        }
        if (!StringUtils.hasText(reason) || rejectionDate == null) {
            throw new ValidationException("Причина отказа и дата должны быть указаны");
        }
        if (rejectionDate.isBefore(existLog.getStartDate()) || rejectionDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата отказа не может быть раньше начальной или позже сегодняшней даты");
        }
        existLog.setEndDate(rejectionDate);
        existLog.setRejectionReason(reason);
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
        ColumnUsageLog updatedLog = mapper.updateFromDto(existLog,request);
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
}
