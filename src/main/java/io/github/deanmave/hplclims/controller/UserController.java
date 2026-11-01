package io.github.deanmave.hplclims.controller;

import io.github.deanmave.hplclims.domain.UserPosition;
import io.github.deanmave.hplclims.domain.UserRole;
import io.github.deanmave.hplclims.domain.dto.request.UserCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.UserUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.UserResponseDto;
import io.github.deanmave.hplclims.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserResponseDto> getUsers(@RequestParam(required = false) Boolean active,
                                          @RequestParam(required = false) UserRole role,
                                          @RequestParam(required = false) UserPosition position) {
        return service.getUsersFiltered(active, role, position);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable Long userId) {
        return service.getById(userId);
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PatchMapping("/{userId}/status")
    public UserResponseDto changeStatus(@PathVariable Long userId,
                                        @RequestParam boolean status) {
        return service.changeStatus(userId, status);
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PatchMapping("/{userId}/password")
    public UserResponseDto changePassword(@PathVariable Long userId,
                                          @RequestBody String password) {
        return service.changePassword(userId, password);
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto addNewUser(@Valid @RequestBody UserCreateDto createDto) {
        return service.create(createDto);
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PatchMapping("/{userId}")
    public UserResponseDto updateProfile(@PathVariable Long userId,
                                         @Valid @RequestBody UserUpdateDto updateDto) {
        return service.updateProfile(userId, updateDto);
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forceDeleteById(@PathVariable Long userId) {
        service.forceDeleteById(userId);
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PatchMapping("/{userId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeleteById(@PathVariable Long userId) {
        service.softDeleteById(userId);
    }
}
