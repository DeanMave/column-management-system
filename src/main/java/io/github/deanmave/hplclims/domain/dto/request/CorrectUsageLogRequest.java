package io.github.deanmave.hplclims.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CorrectUsageLogRequest {
    private String taskNumber;
    private String drugName;
    private String analysisParameters;
    private String storagePhase;
    private Integer minPressure;
    private Integer maxPressure;
    private LocalDate endDate;
}
