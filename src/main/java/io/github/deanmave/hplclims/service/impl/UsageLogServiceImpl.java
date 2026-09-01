package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.ColumnUsageLog;
import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.dto.request.EndUsageRequest;
import io.github.deanmave.hplclims.domain.dto.request.StartUsageRequest;
import io.github.deanmave.hplclims.exception.ConflictException;
import io.github.deanmave.hplclims.exception.NotFoundException;
import io.github.deanmave.hplclims.exception.ValidationException;
import io.github.deanmave.hplclims.repository.UsageLogRepository;
import io.github.deanmave.hplclims.service.interfaces.ColumnService;
import io.github.deanmave.hplclims.service.interfaces.UsageLogService;
import io.github.deanmave.hplclims.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsageLogServiceImpl implements UsageLogService {
    private final UsageLogRepository repository;
    private final ColumnService columnService;
    private final UserService userService;

    @Override
    @Transactional
    public ColumnUsageLog startUsage(Long userId, Long hplcColumnId, StartUsageRequest request) {
        log.info("Попытка взять пользователем:{} колонку:{} в работу", userId, hplcColumnId);
        User user = userService.getById(userId);
        if (!user.isActive()) {
            throw new ValidationException("Только действующие сотрудники могут брать колонку в работу");
        }
        HplcColumn hplcColumn = columnService.getById(hplcColumnId);
        if (!hplcColumn.isAvailable()) {
            throw new ConflictException("Колонка с id: " + hplcColumnId + " сейчас занята");
        }
        if (!StringUtils.hasText(request.getTaskNumber()) || !StringUtils.hasText(request.getDrugName())) {
            throw new ValidationException("Поля с номером задания и наименованием препарата должны быть заполнены");
        }
        columnService.changeStatus(hplcColumnId, ColumnStatus.IN_USE);
        ColumnUsageLog newLog = new ColumnUsageLog();
        newLog.setUser(user);
        newLog.setHplcColumn(hplcColumn);
        newLog.setStartDate(LocalDate.now());
        newLog.setTaskNumber(request.getTaskNumber());
        newLog.setDrugName(request.getDrugName());
        ColumnUsageLog savedLog = repository.save(newLog);
        log.info("Лог добавлен:{}", savedLog.getId());
        return savedLog;
    }

    @Override
    @Transactional
    public ColumnUsageLog endUsage(Long logId, EndUsageRequest request) {
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
        return endLog;
    }

    @Override
    @Transactional
    public ColumnUsageLog rejectUsage(Long logId, String reason, LocalDate rejectionDate) {
        log.info("Попытка отказа от колонки для лога:{}", logId);
        ColumnUsageLog existLog = repository.findById(logId)
                .orElseThrow(() -> new NotFoundException("Лога с id: " + logId + " не найдено"));
        if (existLog.getEndDate() != null) {
            throw new ConflictException("Анализ по логу с id: " + logId + " уже завершён");
        }
        if(!StringUtils.hasText(reason) || rejectionDate == null){
            throw new ValidationException("Причина отказа и дата должны быть указаны");
        }
        if (rejectionDate.isBefore(existLog.getStartDate()) || rejectionDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата отказа не может быть раньше начальной или позже сегодняшней даты");
        }
        existLog.setEndDate(rejectionDate);
        existLog.setRejectionReason(reason);
        columnService.changeStatus(existLog.getHplcColumn().getId(),ColumnStatus.AVAILABLE);
        ColumnUsageLog savedLog = repository.save(existLog);
        log.info("Отказ от колонки завершен, лог успешно закрыт:{}",savedLog.getId());
        return savedLog;
    }

    @Override
    public List<ColumnUsageLog> getLogsByColumn(Long hplcColumnId) {
        return List.of();
    }

    @Override
    public List<ColumnUsageLog> getLogsByUser(Long userId) {
        return List.of();
    }

    @Override
    public List<ColumnUsageLog> getActiveUsages() {
        return List.of();
    }
}
