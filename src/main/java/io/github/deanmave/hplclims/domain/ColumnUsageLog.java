package io.github.deanmave.hplclims.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ColumnUsageLog {
    private Long id;
    private User user;
    private Column column;
    private String taskNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String analysisParameters;
    private String storagePhase;
}
