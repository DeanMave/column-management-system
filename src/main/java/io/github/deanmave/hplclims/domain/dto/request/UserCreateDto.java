package io.github.deanmave.hplclims.domain.dto.request;

import io.github.deanmave.hplclims.domain.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    @NotBlank(message = "Имя сотрудника должно быть заполнено")
    private String firstName;
    @NotBlank(message = "Фамилия сотрудника должно быть заполнено")
    private String lastName;
    @NotBlank(message = "Отчество сотрудника должно быть заполнено")
    private String middleName;
    @NotBlank(message = "Логин не может быть пустым")
    private String login;
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
    @NotBlank(message = "Должность сотрудника должна быть выбрана")
    private UserRole role;
}
