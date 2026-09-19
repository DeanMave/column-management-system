package io.github.deanmave.hplclims.domain.dto.request;

import io.github.deanmave.hplclims.domain.ColumnStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnUpdateDto {
    private String manufacturer;
    private String serialNumber;
    private String partNumber;
    private Integer length;
    private BigDecimal diameter;
    private BigDecimal particleSize;
    private LocalDate installationDate;
    private Double phMin;
    private Double phMax;
    private String stationaryPhase;
    private Integer maxPressure;
    private String ownerOrganization;
    private LocalDate returnDate;
    private ColumnStatus status;
    private String internalCode;
    private String storageLocation;
}
