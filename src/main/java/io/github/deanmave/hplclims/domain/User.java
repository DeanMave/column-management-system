package io.github.deanmave.hplclims.domain;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String login;
    private String password;
    private UserRole role;
}
