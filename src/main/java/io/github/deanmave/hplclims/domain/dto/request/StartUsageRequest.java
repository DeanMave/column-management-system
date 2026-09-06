package io.github.deanmave.hplclims.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartUsageRequest {
    private String taskNumber;
    private String drugName;
}
