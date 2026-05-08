package io.github.deanmave.hplclims.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Column {
    private Long id;
    private String manufacturer;
    private String serialNumber;
    private String partNumber;
    private Integer length;
    private Integer diameter;
    private Double particleSize;
    private LocalDate installationDate;
    private Integer phMin;
    private Integer phMax;
    private String stationaryPhase;
    private Integer maxPressure;
    private String ownerOrganization;
    private LocalDate returnDate;
    private ColumnStatus status;

    public boolean isExternal(){
        return ownerOrganization != null;
    }

    public boolean isAvailable(){
        return status == ColumnStatus.AVAILABLE;
    }
}
