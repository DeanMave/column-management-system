package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.ColumnUsageLog;
import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.dto.request.CorrectUsageLogRequest;
import io.github.deanmave.hplclims.domain.dto.request.EndUsageRequest;
import io.github.deanmave.hplclims.domain.dto.request.StartUsageRequest;
import io.github.deanmave.hplclims.exception.ConflictException;
import io.github.deanmave.hplclims.exception.NotFoundException;
import io.github.deanmave.hplclims.exception.ValidationException;
import io.github.deanmave.hplclims.repository.UsageLogRepository;
import io.github.deanmave.hplclims.service.interfaces.ColumnService;
import io.github.deanmave.hplclims.service.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class UsageLogServiceImplTest {

    @Mock
    private UsageLogRepository repository;

    @Mock
    private ColumnService columnService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UsageLogServiceImpl service;

    private User testUser;
    private HplcColumn testColumn;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setActive(true);

        testColumn = new HplcColumn();
        testColumn.setId(1L);
        testColumn.setStatus(ColumnStatus.AVAILABLE);
    }

    @Nested
    class StartUsage {

        private StartUsageRequest request;

        @BeforeEach
        void setUpStartUsage() {
            request = new StartUsageRequest();
            request.setTaskNumber("2710ДК");
            request.setDrugName("Фенобарбитал");
        }

        @Test
        void whenUserIsInactive_ShouldThrowValidationException() {
            testUser.setActive(false);
            when(userService.getById(1L)).thenReturn(testUser);

            assertThatThrownBy(() -> service.startUsage(1L, 1L, request))
                    .isInstanceOf(ValidationException.class);

            verify(columnService, never()).changeStatus(eq(testColumn.getId()), any());
            verify(repository, never()).save(any());
        }

        @Test
        void whenColumnIsNotAvailable_ShouldThrowConflictException() {
            testColumn.setStatus(ColumnStatus.IN_USE);
            when(userService.getById(1L)).thenReturn(testUser);
            when(columnService.getById(1L)).thenReturn(testColumn);

            assertThatThrownBy(() -> service.startUsage(1L, 1L, request))
                    .isInstanceOf(ConflictException.class);

            verify(columnService, never()).changeStatus(eq(testColumn.getId()), any());
            verify(repository, never()).save(any());
        }

        @Test
        void whenDataIsValid_ShouldCreateLogAndSetColumnInUse() {
            when(userService.getById(1L)).thenReturn(testUser);
            when(columnService.getById(1L)).thenReturn(testColumn);

            ColumnUsageLog savedLogDb = new ColumnUsageLog();
            savedLogDb.setId(1L);
            when(repository.save(any(ColumnUsageLog.class))).thenReturn(savedLogDb);

            ColumnUsageLog result = service.startUsage(1L, 1L, request);

            assertThat(result).isEqualTo(savedLogDb);
            verify(columnService).changeStatus(1L, ColumnStatus.IN_USE);

            ArgumentCaptor<ColumnUsageLog> captor = ArgumentCaptor.forClass(ColumnUsageLog.class);
            verify(repository).save(captor.capture());

            ColumnUsageLog captured = captor.getValue();
            assertThat(captured.getUser()).isEqualTo(testUser);
            assertThat(captured.getHplcColumn()).isEqualTo(testColumn);
            assertThat(captured.getStartDate()).isEqualTo(LocalDate.now());
            assertThat(captured.getTaskNumber()).isEqualTo("2710ДК");
            assertThat(captured.getEndDate()).isNull();
        }
    }

    @Nested
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
        void whenLogNotFound_ShouldThrowNotFoundException() {
            when(repository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.endUsage(10L, request))
                    .isInstanceOf(NotFoundException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        void whenLogAlreadyClosed_ShouldThrowConflictException() {
            existingLog.setEndDate(LocalDate.now().minusDays(1));
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.endUsage(10L, request))
                    .isInstanceOf(ConflictException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        void whenEndDateBeforeStartDate_ShouldThrowValidationException() {
            request.setEndDate(LocalDate.now().minusDays(4));
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.endUsage(10L, request))
                    .isInstanceOf(ValidationException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        void whenEndDateInFuture_ShouldThrowValidationException() {
            request.setEndDate(LocalDate.now().plusDays(2));
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.endUsage(10L, request))
                    .isInstanceOf(ValidationException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        void whenDataIsValid_ShouldCloseLogAndSetColumnAvailable() {
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            ColumnUsageLog savedLogDb = new ColumnUsageLog();
            savedLogDb.setId(10L);
            when(repository.save(existingLog)).thenReturn(savedLogDb);

            ColumnUsageLog result = service.endUsage(10L, request);

            assertThat(result).isEqualTo(savedLogDb);

            assertThat(existingLog.getEndDate()).isEqualTo(request.getEndDate());
            assertThat(existingLog.getAnalysisParameters()).isEqualTo("А - 80% ACN, C - 20% H2O, 1 мл/мин");
            assertThat(existingLog.getStoragePhase()).isEqualTo("80% ACN : 20% H2O");
            assertThat(existingLog.getMinPressure()).isEqualTo(53);
            assertThat(existingLog.getMaxPressure()).isEqualTo(119);

            verify(columnService).changeStatus(testColumn.getId(), ColumnStatus.AVAILABLE);
        }
    }

    @Nested
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
        void whenLogNotFound_ShouldThrowNotFoundException() {
            when(repository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.rejectUsage(10L, rejectReason, LocalDate.now()))
                    .isInstanceOf(NotFoundException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        void whenLogAlreadyClosed_ShouldThrowConflictException() {
            existingLog.setEndDate(LocalDate.now().minusDays(1));
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.rejectUsage(10L, rejectReason, LocalDate.now()))
                    .isInstanceOf(ConflictException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        void whenRejectionDateBeforeStartDate_ShouldThrowValidationException() {
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.rejectUsage(10L, rejectReason, LocalDate.now().minusDays(4)))
                    .isInstanceOf(ValidationException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        void whenRejectionDateInFuture_ShouldThrowValidationException() {
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.rejectUsage(10L, rejectReason, LocalDate.now().plusDays(2)))
                    .isInstanceOf(ValidationException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        void whenDataIsValid_ShouldCloseLogAndSetColumnAvailable() {
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            ColumnUsageLog savedLogDb = new ColumnUsageLog();
            savedLogDb.setId(10L);
            when(repository.save(existingLog)).thenReturn(savedLogDb);

            ColumnUsageLog result = service.rejectUsage(10L, rejectReason, LocalDate.now());

            assertThat(result).isEqualTo(savedLogDb);

            assertThat(existingLog.getEndDate()).isEqualTo(LocalDate.now());
            assertThat(existingLog.getRejectionReason()).isEqualTo("Причина отказа");

            verify(columnService).changeStatus(testColumn.getId(), ColumnStatus.AVAILABLE);
        }
    }


    @Nested
    class CorrectLog {
        private ColumnUsageLog existingLog;
        private CorrectUsageLogRequest request;

        @BeforeEach
        void setRejectUsage() {
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
        }

        @Test
        void whenLogNotFound_ShouldThrowNotFoundException() {
            when(repository.findById(10L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.correctLog(10L, request))
                    .isInstanceOf(NotFoundException.class);

            verify(columnService, never()).changeStatus(anyLong(), any());
            verify(repository, never()).save(any());
        }

        @Test
        void whenEndDateIsInvalid_ShouldThrowValidationException() {
            request.setEndDate(LocalDate.now().minusDays(4));
            existingLog.setEndDate(LocalDate.now().minusDays(1));
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            assertThatThrownBy(() -> service.correctLog(10L, request))
                    .isInstanceOf(ValidationException.class);

            verify(repository, never()).save(any());
        }

        @Test
        void whenDataIsValidAndLogWasActive_ShouldCorrectAndChangeColumnStatus() {
            when(repository.findById(10L)).thenReturn(Optional.of(existingLog));

            when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            ColumnUsageLog result = service.correctLog(10L, request);
            assertThat(existingLog.getTaskNumber()).isEqualTo("2780ДК");
            assertThat(existingLog.getDrugName()).isEqualTo("Валидол");
            assertThat(existingLog.getAnalysisParameters()).isEqualTo("Канал А - 90% H2O; C - 10% ACN");
            assertThat(existingLog.getStoragePhase()).isEqualTo("80% ACN : 20% H2O");
            assertThat(existingLog.getMinPressure()).isEqualTo(45);
            assertThat(existingLog.getMaxPressure()).isEqualTo(140);
            assertThat(existingLog.getEndDate()).isEqualTo(LocalDate.now());

            verify(columnService).changeStatus(testColumn.getId(), ColumnStatus.AVAILABLE);
            verify(repository).save(any());
        }
    }

    @Test
    void getLogsByColumn_WhenColumnNotFound_ShouldThrowNotFoundException() {
        when(columnService.getById(1L)).thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> service.getLogsByColumn(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getLogsByUser_WhenUserNotFound_ShouldThrowNotFoundException() {
        when(userService.getById(1L)).thenThrow(NotFoundException.class);

        assertThatThrownBy(() -> service.getLogsByUser(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getActiveUsages_ShouldReturnOnlyUnfinishedLogs() {
        ColumnUsageLog existLog = new ColumnUsageLog();
        existLog.setId(1L);
        when(repository.findByEndDateIsNull()).thenReturn(List.of(existLog));

        assertThat(service.getActiveUsages()).containsExactly(existLog);
    }
}