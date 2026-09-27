package io.github.deanmave.hplclims.service.interfaces;

import io.github.deanmave.hplclims.domain.UserRole;
import io.github.deanmave.hplclims.domain.dto.request.UserCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.UserUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto create(UserCreateDto userDto);

    List<UserResponseDto> getAll();

    List<UserResponseDto> getByActive(boolean status);

    UserResponseDto getById(Long id);

    UserResponseDto changeStatus(Long id, boolean newStatus);

    UserResponseDto updateProfile(Long id, UserUpdateDto userDto);

    UserResponseDto changePassword(Long id, String newPassword);

    UserResponseDto changeLogin(Long id, String newLogin);

    UserResponseDto changeRole(Long id, UserRole newRole);
}
