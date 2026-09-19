package io.github.deanmave.hplclims.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsageLogResponseDto {
    private Long id;
    private UserShortResponseDto user;
    private ColumnResponseDto hplcColumn;
    private String taskNumber;
    private String drugName;
    private String analysisParameters;
    private String storagePhase;
    private Integer minPressure;
    private Integer maxPressure;
    private LocalDate startDate;
    private LocalDate endDate;
    private String rejectionReason;
}
