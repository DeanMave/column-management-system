package io.github.deanmave.hplclims.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
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
    @Positive(message = "Число должно быть больше нуля")
    private Integer minPressure;
    @Positive(message = "Число должно быть больше нуля")
    private Integer maxPressure;
    @NotNull(message = "Дата окончания анализа не может быть пустой")
    @PastOrPresent(message = "Дата окончания анализа может быть только в прошлом или настоящем")
    private LocalDate endDate;
}
