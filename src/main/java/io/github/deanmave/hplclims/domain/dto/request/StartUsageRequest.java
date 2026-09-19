package io.github.deanmave.hplclims.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartUsageRequest{
    @NotBlank(message = "Номер задания должен быть указан")
    private String taskNumber;
    @NotBlank(message = "Наименование препарата должно быть указано")
    private String drugName;
}