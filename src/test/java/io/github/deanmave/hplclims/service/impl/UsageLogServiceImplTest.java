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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис учёта использования хроматографических колонок")
class UsageLogServiceImplTest {

    @Mock
    private UsageLogRepository repository;

    @Mock
    private ColumnUsageLogMapper mapper;

    @Mock
    private ColumnService columnService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ColumnRepository columnRepository;

    @InjectMocks
    private UsageLogServiceImpl service;

    private User testUser;
    private HplcColumn testColumn;
    private ColumnUsageLog testLog;
    private UsageLogResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setActive(true);

        testColumn = new HplcColumn();
        testColumn.setId(1L);
        testColumn.setStatus(ColumnStatus.AVAILABLE);

        testLog = new ColumnUsageLog();
        testLog.setId(10L);
        testLog.setUser(testUser);
        testLog.setHplcColumn(testColumn);
        testLog.setStartDate(LocalDate.now());

        testResponseDto = new UsageLogResponseDto();
        testResponseDto.setId(10L);
    }

    @Nested
    @DisplayName("startUsage: начало использования колонки")
    class StartUsage {

        private StartUsageRequest request;

        @BeforeEach
        void setUpStartUsage() {
            request = new StartUsageRequest();
            request.setTaskNumber("2710ДК");
            request.setDrugName("Фенобарбитал");
        }

        @Test
        @DisplayName("выброс ValidationException, если пользователь неактивен")
        void whenUserIsInactive_ShouldThrowValidationException() {
            testUser.setActive(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            assertThatThrownBy(() -> service.startUsage(1L, 1L, request))
                    .isInstanceOf(ValidationException.class);

            verify(columnService, never()).changeStatus(eq(testColumn.getId()), any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("выброс ConflictException, если колонка занята")
        void whenColumnIsNotAvailable_ShouldThrowConflictException() {
            testColumn.setStatus(ColumnStatus.IN_USE);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(columnRepository.findById(1L)).thenReturn(Optional.of(testColumn));

            assertThatThrownBy(() -> service.startUsage(1L, 1L, request))
                    .isInstanceOf(ConflictException.class);

            verify(columnService, never()).changeStatus(eq(testColumn.getId()), any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("выброс ValidationException, если не заполнены обязательные поля запроса")
        void whenRequestFieldsAreBlank_ShouldThrowValidationException() {
            request.setTaskNumber("");
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(columnRepository.findById(1L)).thenReturn(Optional.of(testColumn));

            assertThatThrownBy(() -> service.startUsage(1L, 1L, request))
                    .isInstanceOf(ValidationException.class);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("успешное создание лога и смена статуса колонки в IN_USE")
        void whenDataIsValid_ShouldCreateLogAndSetColumnInUse() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(columnRepository.findById(1L)).thenReturn(Optional.of(testColumn));
            when(mapper.toColumnUsageLog(request, testUser, testColumn)).thenReturn(testLog);
            when(repository.save(testLog)).thenReturn(testLog);
            when(mapper.toUsageLogResponseDto(testLog)).thenReturn(testResponseDto);

            UsageLogResponseDto result = service.startUsage(1L, 1L, request);

            assertThat(result).isEqualTo(testResponseDto);
            verify(columnService).changeStatus(1L, ColumnStatus.IN_USE);
            verify(repository).save(testLog);
            assertThat(testLog.getStartDate()).isEqualTo(LocalDate.now());
        }
    }

    @Nested
    @DisplayName("endUsage: конец пользования колонкой")
    class EndUsage {

        private ColumnUsageLog existingLog;
        private EndUsageRequest request;

        @BeforeEach
        void setUpEndUsage() {
            existingLog = new ColumnUsageLog();
            existingLog.setId(10L);
            existingLog.setUser(testUser);
            existingLog.setHplcColumn(testColumn);
            existingLog.setStartDate(LocalDate.now().minusDays(3));

            request = new EndUsageRequest();
            request.setAnalysisParameters("А - 80% ACN, C - 20% H2O, 1 мл/мин");
            request.setStoragePhase("80% ACN : 20% H2O");
            request.setMinPressure(53);
            request.setMaxPressure(119);
            request.setEndDate(LocalDate.now());
        }

        @Test
        @DisplayName("выброс NotFoundException, если лог не найден")
        void whenLogNotFound_ShouldThrowNotFoundException() {
            when(repository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.endUsage(10L, request))
                    .isInstanceOf(NotFoundException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("выброс ConflictException, если лог уже закрыт")
        void whenLogAlreadyClosed_ShouldThrowConflictException() {
            existingLog.setEndDate(LocalDate.now().minusDays(1));
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.endUsage(10L, request))
                    .isInstanceOf(ConflictException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("выброс ValidationException, если дата окончания раньше даты начала")
        void whenEndDateBeforeStartDate_ShouldThrowValidationException() {
            request.setEndDate(LocalDate.now().minusDays(4));
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.endUsage(10L, request))
                    .isInstanceOf(ValidationException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("выброс ValidationException, если дата завершения позже сегодняшней")
        void whenEndDateInFuture_ShouldThrowValidationException() {
            request.setEndDate(LocalDate.now().plusDays(2));
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.endUsage(10L, request))
                    .isInstanceOf(ValidationException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("успешное завершение лога и смена статуса колонки в AVAILABLE")
        void whenDataIsValid_ShouldCloseLogAndSetColumnAvailable() {
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));
            when(repository.save(existingLog)).thenReturn(existingLog);
            when(mapper.toUsageLogResponseDto(existingLog)).thenReturn(testResponseDto);

            UsageLogResponseDto result = service.endUsage(10L, request);

            assertThat(result).isEqualTo(testResponseDto);

            assertThat(existingLog.getEndDate()).isEqualTo(request.getEndDate());
            assertThat(existingLog.getAnalysisParameters()).isEqualTo("А - 80% ACN, C - 20% H2O, 1 мл/мин");
            assertThat(existingLog.getStoragePhase()).isEqualTo("80% ACN : 20% H2O");
            assertThat(existingLog.getMinPressure()).isEqualTo(53);
            assertThat(existingLog.getMaxPressure()).isEqualTo(119);

            verify(columnService).changeStatus(testColumn.getId(), ColumnStatus.AVAILABLE);
            verify(repository).save(existingLog);
        }
    }

    @Nested
    @DisplayName("rejectUsage: отказ от колонки")
    class RejectUsage {
        private ColumnUsageLog existingLog;
        private final String rejectReason = "Причина отказа";

        @BeforeEach
        void setRejectUsage() {
            existingLog = new ColumnUsageLog();
            existingLog.setId(10L);
            existingLog.setUser(testUser);
            existingLog.setHplcColumn(testColumn);
            existingLog.setStartDate(LocalDate.now().minusDays(3));
        }

        @Test
        @DisplayName("выброс NotFoundException, если лог не найден")
        void whenLogNotFound_ShouldThrowNotFoundException() {
            when(repository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.rejectUsage(10L, rejectReason, LocalDate.now()))
                    .isInstanceOf(NotFoundException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("выброс ConflictException, если лог уже закрыт")
        void whenLogAlreadyClosed_ShouldThrowConflictException() {
            existingLog.setEndDate(LocalDate.now().minusDays(1));
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.rejectUsage(10L, rejectReason, LocalDate.now()))
                    .isInstanceOf(ConflictException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("выброс ValidationException, если дата отказа раньше даты начала")
        void whenRejectionDateBeforeStartDate_ShouldThrowValidationException() {
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.rejectUsage(10L, rejectReason, LocalDate.now().minusDays(4)))
                    .isInstanceOf(ValidationException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("выброс ValidationException, если дата отказа позже сегодняшней даты")
        void whenRejectionDateInFuture_ShouldThrowValidationException() {
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.rejectUsage(10L, rejectReason, LocalDate.now().plusDays(2)))
                    .isInstanceOf(ValidationException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("успешное завершение лога и смена статуса колонки в AVAILABLE")
        void whenDataIsValid_ShouldCloseLogAndSetColumnAvailable() {
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));
            when(repository.save(existingLog)).thenReturn(existingLog);
            when(mapper.toUsageLogResponseDto(existingLog)).thenReturn(testResponseDto);

            UsageLogResponseDto result = service.rejectUsage(10L, rejectReason, LocalDate.now());

            assertThat(result).isEqualTo(testResponseDto);

            assertThat(existingLog.getEndDate()).isEqualTo(LocalDate.now());
            assertThat(existingLog.getRejectionReason()).isEqualTo("Причина отказа");

            verify(columnService).changeStatus(testColumn.getId(), ColumnStatus.AVAILABLE);
            verify(repository).save(existingLog);
        }
    }


    @Nested
    @DisplayName("correctLog: корректировка записи лога")
    class CorrectLog {
        private ColumnUsageLog existingLog;
        private CorrectUsageLogRequest request;

        @BeforeEach
        void setUpCorrectLog() {
            existingLog = new ColumnUsageLog();
            existingLog.setId(10L);
            existingLog.setUser(testUser);
            existingLog.setHplcColumn(testColumn);
            existingLog.setStartDate(LocalDate.now().minusDays(3));

            request = new CorrectUsageLogRequest();
            request.setTaskNumber("2780ДК");
            request.setDrugName("Валидол");
            request.setAnalysisParameters("Канал А - 90% H2O; C - 10% ACN");
            request.setStoragePhase("80% ACN : 20% H2O");
            request.setMinPressure(45);
            request.setMaxPressure(140);
            request.setEndDate(LocalDate.now());

            testResponseDto.setTaskNumber("2780ДК");
            testResponseDto.setDrugName("Валидол");
            testResponseDto.setAnalysisParameters("Канал А - 90% H2O; C - 10% ACN");
            testResponseDto.setStoragePhase("80% ACN : 20% H2O");
            testResponseDto.setMinPressure(45);
            testResponseDto.setMaxPressure(140);
            testResponseDto.setEndDate(LocalDate.now());
        }

        @Test
        @DisplayName("выброс NotFoundException, если лог не найден")
        void whenLogNotFound_ShouldThrowNotFoundException() {
            when(repository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.correctLog(10L, request))
                    .isInstanceOf(NotFoundException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("выброс ValidationException, если дата завершение неверная")
        void whenEndDateIsInvalid_ShouldThrowValidationException() {
            request.setEndDate(LocalDate.now().minusDays(4));
            existingLog.setEndDate(null);
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.correctLog(10L, request))
                    .isInstanceOf(ValidationException.class);

            verify(repository, never()).save(any());
        }

        @Test
        @DisplayName("успешное корректировка неактивного лога")
        void whenDataIsValidAndLogWasNotActive_ShouldCorrect() {
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));
            when(mapper.updateFromDto(existingLog,request)).thenAnswer(invocation->{
                existingLog.setTaskNumber("2780ДК");
                existingLog.setDrugName("Валидол");
                existingLog.setAnalysisParameters("Канал А - 90% H2O; C - 10% ACN");
                existingLog.setStoragePhase("80% ACN : 20% H2O");
                existingLog.setMinPressure(45);
                existingLog.setMaxPressure(140);
                existingLog.setEndDate(LocalDate.now());
                return existingLog;
            });
            when(mapper.toUsageLogResponseDto(existingLog)).thenReturn(testResponseDto);
            when(repository.save(existingLog)).thenReturn(existingLog);

            service.correctLog(10L, request);
            assertThat(existingLog.getTaskNumber()).isEqualTo("2780ДК");
            assertThat(existingLog.getDrugName()).isEqualTo("Валидол");
            assertThat(existingLog.getAnalysisParameters()).isEqualTo("Канал А - 90% H2O; C - 10% ACN");
            assertThat(existingLog.getStoragePhase()).isEqualTo("80% ACN : 20% H2O");
            assertThat(existingLog.getMinPressure()).isEqualTo(45);
            assertThat(existingLog.getMaxPressure()).isEqualTo(140);
            assertThat(existingLog.getEndDate()).isEqualTo(LocalDate.now());

            verify(repository).save(existingLog);
        }
    }

    @Test
    @DisplayName("getLogsByColumn: выброс NotFoundException, если колонка не найдена")
    void getLogsByColumn_WhenColumnNotFound_ShouldThrowNotFoundException() {
        when(columnService.getById(1L)).thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> service.getLogsByColumn(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("getLogsByUser: выброс NotFoundException, если пользователь не найден")
    void getLogsByUser_WhenUserNotFound_ShouldThrowNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getLogsByUser(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("getActiveUsages: получение списка незавершенных логов")
    void getActiveUsages_ShouldReturnOnlyUnfinishedLogs() {
        when(repository.findByEndDateIsNull()).thenReturn(List.of(testLog));
        when(mapper.toUsageLogResponseDto(any())).thenReturn(testResponseDto);

        assertThat(service.getActiveUsages()).containsExactly(testResponseDto);
    }
}