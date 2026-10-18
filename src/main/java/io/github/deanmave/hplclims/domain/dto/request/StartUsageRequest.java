package io.github.deanmave.hplclims.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartUsageRequest {
    @NotNull(message = "ID пользователя обязателен")
    private Long userId;
    @NotNull(message = "ID колонки обязателен")
    private Long columnId;
    @NotBlank(message = "Номер задания должен быть указан")
    private String taskNumber;
    @NotBlank(message = "Наименование препарата должно быть указано")
    private String drugName;
}