package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.exception.ConflictException;
import io.github.deanmave.hplclims.exception.NotFoundException;
import io.github.deanmave.hplclims.repository.ColumnRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;

@ExtendWith(MockitoExtension.class)
class ColumnServiceImplTest {

    @Mock
    private ColumnRepository repository;

    @InjectMocks
    private ColumnServiceImpl service;

    private HplcColumn testColumn;

    @BeforeEach
    void setUp() {
        testColumn = new HplcColumn();
        testColumn.setManufacturer("Waters");
        testColumn.setSerialNumber("S409123");
        testColumn.setPartNumber("P9130");
        testColumn.setLength(250);
        testColumn.setDiameter(BigDecimal.valueOf(4.6));
        testColumn.setParticleSize(BigDecimal.valueOf(5));
        testColumn.setInstallationDate(LocalDate.of(2026, 8, 9));
        testColumn.setPhMin(3.0);
        testColumn.setPhMax(7.0);
        testColumn.setStationaryPhase("C18");
        testColumn.setMaxPressure(400);
        testColumn.setOwnerOrganization("Альтаир");
        testColumn.setInternalCode("WATERS-2026-001");
    }

    @Test
    void create_whenInternalCodeIsUnique_shouldSaveAndReturnColumn() {
        testColumn.setStatus(ColumnStatus.RETURNED);
        when(repository.existsByInternalCode(testColumn.getInternalCode())).thenReturn(false);

        HplcColumn savedColumnDb = new HplcColumn();
        savedColumnDb.setId(1L);
        savedColumnDb.setInternalCode(testColumn.getInternalCode());
        when(repository.save(testColumn)).thenReturn(savedColumnDb);

        HplcColumn result = service.create(testColumn);

        assertThat(result).isEqualTo(savedColumnDb);
        assertThat(testColumn.getStatus()).isEqualTo(ColumnStatus.AVAILABLE);
        verify(repository).save(testColumn);
    }

    @Test
    void create_whenInternalCodeAlreadyExists_shouldThrowConflictException() {
        when(repository.existsByInternalCode(testColumn.getInternalCode())).thenReturn(true);

        assertThatThrownBy(() -> service.create(testColumn)).isInstanceOf(ConflictException.class)
                .hasMessageContaining(testColumn.getInternalCode());

        verify(repository, never()).save(any(HplcColumn.class));
    }

    @Test
    void getAll_WhenColumnsExist_ShouldReturnListOfColumns() {
        when(repository.findAll()).thenReturn(List.of(testColumn));

        assertThat(service.getAll()).containsExactly(testColumn);
    }

    @Test
    void getAll_WhenNoColumnsExist_ShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertThat(service.getAll()).isEmpty();
    }

    @Test
    void getById_WhenColumnExists_ShouldReturnColumn() {
        when(repository.findById(1L)).thenReturn(Optional.of(testColumn));

        assertThat(service.getById(1L)).isEqualTo(testColumn);
    }

    @Test
    void getById_WhenColumnDoesNotExist_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void deleteById_WhenColumnExists_ShouldDeleteSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteById(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteById_WhenColumnDoesNotExist_ShouldThrowNotFoundException() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteById(1L)).isInstanceOf(NotFoundException.class);

        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void changeStatus_WhenColumnExists_ShouldUpdateStatusAndReturnColumn() {
        when(repository.findById(1L)).thenReturn(Optional.of(testColumn));

        HplcColumn savedColumnDb = new HplcColumn();
        savedColumnDb.setId(1L);
        savedColumnDb.setStatus(ColumnStatus.IN_USE);
        when(repository.save(testColumn)).thenReturn(savedColumnDb);

        HplcColumn result = service.changeStatus(1L, ColumnStatus.IN_USE);

        assertThat(result).isEqualTo(savedColumnDb);
        assertThat(testColumn.getStatus()).isEqualTo(ColumnStatus.IN_USE);
    }

    @Test
    void changeStatus_WhenColumnDoesNotExist_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeStatus(1L, ColumnStatus.IN_USE))
                .isInstanceOf(NotFoundException.class);
        verify(repository, never()).save(any(HplcColumn.class));
    }

    @Test
    void correctData_WhenColumnExists_ShouldUpdateFieldsAndReturnColumn() {
        when(repository.findById(1L)).thenReturn(Optional.of(testColumn));

        HplcColumn newData = new HplcColumn();
        newData.setManufacturer("Zorbax");
        newData.setSerialNumber("H1235");
        newData.setPartNumber("Z3131");
        newData.setStationaryPhase("C12");
        newData.setInternalCode(testColumn.getInternalCode());

        HplcColumn savedColumnDb = new HplcColumn();
        savedColumnDb.setId(1L);

        when(repository.save(testColumn)).thenReturn(savedColumnDb);

        HplcColumn result = service.correctData(1L, newData);

        assertThat(result).isEqualTo(savedColumnDb);
        assertThat(testColumn.getManufacturer()).isEqualTo("Zorbax");
        assertThat(testColumn.getSerialNumber()).isEqualTo("H1235");
        assertThat(testColumn.getPartNumber()).isEqualTo("Z3131");
        assertThat(testColumn.getStationaryPhase()).isEqualTo("C12");
    }

    @Test
    void correctData_WhenColumnDoesNotExist_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.correctData(1L,testColumn))
                .isInstanceOf(NotFoundException.class);

        verify(repository,never()).save(any(HplcColumn.class));
    }

    @Test
    void correctData_WhenNewInternalCodeIsTaken_ShouldThrowConflictException() {
        when(repository.findById(1L)).thenReturn(Optional.of(testColumn));

        HplcColumn newData = new HplcColumn();
        newData.setInternalCode("ZORBAX-2026-001");

        when(repository.existsByInternalCode(newData.getInternalCode())).thenReturn(true);

        assertThatThrownBy(() -> service.correctData(1L, newData))
                .isInstanceOf(ConflictException.class);

        verify(repository,never()).save(any(HplcColumn.class));

        assertThat(testColumn.getManufacturer()).isEqualTo("Waters");
    }

    @Test
    void correctData_WhenInternalCodeUnchanged_ShouldNotCheckUniqueness() {
        when(repository.findById(1L)).thenReturn(Optional.of(testColumn));

        HplcColumn newData = new HplcColumn();
        newData.setInternalCode(testColumn.getInternalCode());

        service.correctData(1L, newData);

        verify(repository,never()).existsByInternalCode(anyString());
    }
}