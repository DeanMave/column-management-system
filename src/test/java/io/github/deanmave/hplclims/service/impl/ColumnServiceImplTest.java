package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.domain.dto.request.ColumnCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.ColumnUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.ColumnResponseDto;
import io.github.deanmave.hplclims.domain.mapper.HplcColumnMapper;
import io.github.deanmave.hplclims.exception.NotFoundException;
import io.github.deanmave.hplclims.repository.ColumnRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис работы с хроматографическими колонками")
class ColumnServiceImplTest {

    @Mock
    private ColumnRepository repository;

    @Mock
    private HplcColumnMapper mapper;

    @InjectMocks
    private ColumnServiceImpl service;

    private HplcColumn testColumn;
    private ColumnCreateDto testCreateDto;
    private ColumnUpdateDto testUpdateDto;
    private ColumnResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        testColumn = new HplcColumn();
        testColumn.setManufacturer("Waters");
        testColumn.setSerialNumber("S409123");
        testColumn.setPartNumber("P9130");
        testColumn.setLength(250);
        testColumn.setDiameter(BigDecimal.valueOf(4.6));
        testColumn.setParticleSize(BigDecimal.valueOf(5));
        testColumn.setInstallationDate(LocalDate.now());
        testColumn.setPhMin(3.0);
        testColumn.setPhMax(7.0);
        testColumn.setStationaryPhase("C18");
        testColumn.setMaxPressure(400);

        testCreateDto = new ColumnCreateDto();
        testCreateDto.setManufacturer("Waters");
        testCreateDto.setSerialNumber("S409123");
        testCreateDto.setPartNumber("P9130");
        testCreateDto.setLength(250);
        testCreateDto.setDiameter(BigDecimal.valueOf(4.6));
        testCreateDto.setParticleSize(BigDecimal.valueOf(5));
        testCreateDto.setInstallationDate(LocalDate.now());
        testCreateDto.setPhMin(3.0);
        testCreateDto.setPhMax(7.0);
        testCreateDto.setStationaryPhase("C18");
        testCreateDto.setMaxPressure(400);
        testCreateDto.setOwnerOrganization("Альтаир");

        testUpdateDto = new ColumnUpdateDto();
        testUpdateDto.setSerialNumber("S444821");
        testUpdateDto.setPartNumber("F6489");
        testUpdateDto.setLength(150);
        testUpdateDto.setDiameter(BigDecimal.valueOf(4.0));

        testResponseDto = new ColumnResponseDto();
        testResponseDto.setManufacturer("Waters");
        testResponseDto.setSerialNumber("S409123");
        testResponseDto.setPartNumber("P9130");
        testResponseDto.setLength(250);
        testResponseDto.setDiameter(BigDecimal.valueOf(4.6));
        testResponseDto.setParticleSize(BigDecimal.valueOf(5));
        testResponseDto.setInstallationDate(LocalDate.now());
        testResponseDto.setPhMin(3.0);
        testResponseDto.setPhMax(7.0);
        testResponseDto.setStationaryPhase("C18");
        testResponseDto.setMaxPressure(400);
    }

    @Test
    @DisplayName("create: создание внутренней колонки — генерация кода INT-YYYY-XXX")
    void create_whenColumnIsInternal_shouldGenerateIntCode() {
        testColumn.setOwnerOrganization(null);

        int currentYear = LocalDate.now().getYear();
        String expectedCode = String.format("INT-%d-001", currentYear);

        when(mapper.toHplcColumn(testCreateDto)).thenReturn(testColumn);
        when(repository.getNextIntSequenceValue()).thenReturn(1L);
        when(repository.save(testColumn)).thenReturn(testColumn);
        when(mapper.toColumnResponseDto(testColumn)).thenReturn(testResponseDto);

        service.create(testCreateDto);

        assertThat(testColumn.getInternalCode()).isEqualTo(expectedCode);
        verify(repository).getNextIntSequenceValue();
    }

    @Test
    @DisplayName("create: создание внешней колонки — генерация кода EXT-YYYY-XXX")
    void create_whenColumnIsExternal_shouldGenerateExtCode() {
        testColumn.setOwnerOrganization("Альтаир");

        int currentYear = LocalDate.now().getYear();
        String expectedCode = String.format("EXT-%d-001", currentYear);

        when(mapper.toHplcColumn(testCreateDto)).thenReturn(testColumn);
        when(repository.getNextExtSequenceValue()).thenReturn(1L);
        when(repository.save(testColumn)).thenReturn(testColumn);
        when(mapper.toColumnResponseDto(testColumn)).thenReturn(testResponseDto);

        service.create(testCreateDto);

        assertThat(testColumn.getInternalCode()).isEqualTo(expectedCode);
        verify(repository).getNextExtSequenceValue();
    }

    @Test
    @DisplayName("getAll: получение списка колонок при их наличии в БД")
    void getAll_WhenColumnsExist_ShouldReturnListOfColumns() {
        when(mapper.toColumnResponseDto(testColumn)).thenReturn(testResponseDto);
        when(repository.findAll()).thenReturn(List.of(testColumn));

        assertThat(service.getAll()).containsExactly(testResponseDto);
    }

    @Test
    @DisplayName("getAll: получение пустого списка, если колонки в БД отсутствуют")
    void getAll_WhenNoColumnsExist_ShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertThat(service.getAll()).isEmpty();
    }

    @Test
    @DisplayName("getById: получение колонки по существующему ID")
    void getById_WhenColumnExists_ShouldReturnColumn() {
        when(mapper.toColumnResponseDto(testColumn)).thenReturn(testResponseDto);
        when(repository.findById(1L)).thenReturn(Optional.of(testColumn));

        assertThat(service.getById(1L)).isEqualTo(testResponseDto);
    }

    @Test
    @DisplayName("getById: выброс NotFoundException при поиске несуществующего ID")
    void getById_WhenColumnDoesNotExist_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("deleteById: успешное удаление колонки по существующему ID")
    void deleteById_WhenColumnExists_ShouldDeleteSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        service.deleteById(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteById: выброс NotFoundException при попытке удалить несуществующую колонку")
    void deleteById_WhenColumnDoesNotExist_ShouldThrowNotFoundException() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteById(1L)).isInstanceOf(NotFoundException.class);

        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("changeStatus: обновление статуса существующей колонки")
    void changeStatus_WhenColumnExists_ShouldUpdateStatusAndReturnColumn() {
        testResponseDto.setStatus(ColumnStatus.IN_USE);

        when(mapper.toColumnResponseDto(testColumn)).thenReturn(testResponseDto);
        when(repository.findById(1L)).thenReturn(Optional.of(testColumn));
        when(repository.save(testColumn)).thenReturn(testColumn);
        when(mapper.toColumnResponseDto(testColumn)).thenReturn(testResponseDto);

        service.changeStatus(1L, ColumnStatus.IN_USE);

        assertThat(testColumn.getStatus()).isEqualTo(ColumnStatus.IN_USE);
    }

    @Test
    @DisplayName("changeStatus: выброс NotFoundException при попытке сменить статус несуществующей колонки")
    void changeStatus_WhenColumnDoesNotExist_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeStatus(1L, ColumnStatus.IN_USE))
                .isInstanceOf(NotFoundException.class);
        verify(repository, never()).save(any(HplcColumn.class));
    }

    @Test
    @DisplayName("correctData: успешная корректировка полей колонки")
    void correctData_WhenColumnExists_ShouldUpdateFieldsAndReturnColumn() {
        when(repository.findById(1L)).thenReturn(Optional.of(testColumn));
        when(mapper.updateFromDto(testColumn, testUpdateDto)).thenAnswer(invocation -> {
            testColumn.setPartNumber("F6489");
            testColumn.setSerialNumber("S444821");
            testColumn.setLength(150);
            testColumn.setDiameter(BigDecimal.valueOf(4.0));
            return testColumn;
        });
        when(repository.save(testColumn)).thenReturn(testColumn);
        when(mapper.toColumnResponseDto(testColumn)).thenReturn(testResponseDto);

        service.correctData(1L, testUpdateDto);

        assertThat(testColumn.getPartNumber()).isEqualTo("F6489");
        assertThat(testColumn.getSerialNumber()).isEqualTo("S444821");
        assertThat(testColumn.getLength()).isEqualTo(150);
        assertThat(testColumn.getDiameter()).isEqualTo(BigDecimal.valueOf(4.0));
    }

    @Test
    @DisplayName("correctData: выброс NotFoundException при попытке скорректировать несуществующую колонку")
    void correctData_WhenColumnDoesNotExist_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.correctData(1L, testUpdateDto))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any(HplcColumn.class));
    }
}