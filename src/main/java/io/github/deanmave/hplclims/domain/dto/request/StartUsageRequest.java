package io.github.deanmave.hplclims.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StartUsageRequest {
    private String taskNumber;
    private String drugName;
}
