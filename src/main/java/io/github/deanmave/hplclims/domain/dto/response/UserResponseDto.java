package io.github.deanmave.hplclims.domain.dto.response;

import io.github.deanmave.hplclims.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String login;
    private UserRole role;
    private boolean isActive;
}
