package io.github.deanmave.hplclims.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RejectUsageRequest {
    @NotBlank(message = "Причина отказа должна быть указана")
    private String reason;
    @NotNull(message = "Дата должна быть указана")
    private LocalDate date;
}
