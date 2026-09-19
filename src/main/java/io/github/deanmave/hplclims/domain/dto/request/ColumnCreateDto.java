package io.github.deanmave.hplclims.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ColumnCreateDto {
    @NotBlank(message = "Производитель колонки должен быть указан")
    private String manufacturer;
    @NotBlank(message = "Номер серии колонки должен быть указан")
    private String serialNumber;
    @NotBlank(message = "Номер партии колонки должен быть указан")
    private String partNumber;
    @NotNull(message = "Длина колонки должен быть указан")
    @Positive(message = "Число должно быть больше нуля")
    private Integer length;
    @NotNull(message = "Диаметр колонки должен быть указан")
    @Positive(message = "Число должно быть больше нуля")
    private BigDecimal diameter;
    @NotNull(message = "Размер частиц колонки должен быть указан")
    @Positive(message = "Число должно быть больше нуля")
    private BigDecimal particleSize;
    @NotNull(message = "Дата установки колонки должна быть указана")
    private LocalDate installationDate;
    @NotNull(message = "Минимальная pH колонки должна быть указана")
    @Positive(message = "Число должно быть больше нуля")
    private Double phMin;
    @NotNull(message = "Максимальная pH колонки должна быть указана")
    @Positive(message = "Число должно быть больше нуля")
    private Double phMax;
    @NotBlank(message = "Неподвижная фаза колонки должна быть указана")
    private String stationaryPhase;
    @NotNull(message = "Максимальный порог давления должен быть указан")
    @Positive(message = "Число должно быть больше нуля")
    private Integer maxPressure;
    private String ownerOrganization;
    private LocalDate returnDate;
    private String internalCode;
    private String storageLocation;
}
