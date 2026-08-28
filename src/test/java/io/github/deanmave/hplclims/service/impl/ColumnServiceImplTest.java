package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import io.github.deanmave.hplclims.domain.HplcColumn;
import io.github.deanmave.hplclims.repository.ColumnRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        testColumn.setManufacturer("Agilent");
        testColumn.setSerialNumber("S409123");
        testColumn.setPartNumber("P9130");
        testColumn.setLength(250);
        testColumn.setDiameter(BigDecimal.valueOf(4.6));
        testColumn.setParticleSize(BigDecimal.valueOf(5));
        testColumn.setInstallationDate(LocalDate.of(2026,8,9));
        testColumn.setPhMin(3);
        testColumn.setPhMax(7);
        testColumn.setStationaryPhase("C18");
        testColumn.setMaxPressure(400);
        testColumn.setOwnerOrganization("Альтаир");
        testColumn.setInternalCode("HPLC001");
    }

    @Test
    void create_whenInternalCodeIsUnique_shouldSaveAndReturnColumn() {
    }

    @Test
    void create_whenInternalCodeAlreadyExists_shouldThrowConflictException() {
    }

    @Test
    void getAll() {
    }

    @Test
    void getById() {
    }

    @Test
    void deleteById() {
    }

    @Test
    void changeStatus() {
    }

    @Test
    void correctData() {
    }
}