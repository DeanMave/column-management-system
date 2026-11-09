package io.github.deanmave.hplclims.service.interfaces;

import io.github.deanmave.hplclims.domain.UserPosition;
import io.github.deanmave.hplclims.domain.UserRole;
import io.github.deanmave.hplclims.domain.dto.request.UserCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.UserUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponseDto create(UserCreateDto userDto);

    Page<UserResponseDto> getUsersFiltered(Boolean status, UserRole role, UserPosition position, Pageable pageable);

    UserResponseDto getById(Long id);

    UserResponseDto changeStatus(Long id, boolean newStatus);

    UserResponseDto updateProfile(Long id, UserUpdateDto userDto);

    UserResponseDto changePassword(Long id, String newPassword);

    void forceDeleteById(Long id);

    void softDeleteById(Long id);
}
