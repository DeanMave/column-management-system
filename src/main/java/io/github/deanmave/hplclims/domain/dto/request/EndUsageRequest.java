package io.github.deanmave.hplclims.domain.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class EndUsageRequest {
    private String analysisParameters;
    private String storagePhase;
    private Integer minPressure;
    private Integer maxPressure;
    private LocalDate endDate;
}
