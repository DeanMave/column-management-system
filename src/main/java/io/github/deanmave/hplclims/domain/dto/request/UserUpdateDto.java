package io.github.deanmave.hplclims.domain.dto.request;

import io.github.deanmave.hplclims.domain.UserPosition;
import io.github.deanmave.hplclims.domain.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {
    private String login;
    private String firstName;
    private String lastName;
    private String middleName;
    private UserRole role;
    private UserPosition position;
}
