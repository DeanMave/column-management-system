package io.github.deanmave.hplclims.controller;

import io.github.deanmave.hplclims.domain.UserPosition;
import io.github.deanmave.hplclims.domain.UserRole;
import io.github.deanmave.hplclims.domain.dto.request.UserCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.UserUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.UserResponseDto;
import io.github.deanmave.hplclims.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/users")
@SecurityRequirement(name = "BearerAuth")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public Page<UserResponseDto> getUsers(@RequestParam(required = false) Boolean active,
                                          @RequestParam(required = false) UserRole role,
                                          @RequestParam(required = false) UserPosition position,
                                          @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.getUsersFiltered(active, role, position,pageable);
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable Long userId) {
        return service.getById(userId);
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'LAB_HEAD')")
    @PatchMapping("/{userId}/status")
    public UserResponseDto changeStatus(@PathVariable Long userId,
                                        @RequestParam boolean status) {
        return service.changeStatus(userId, status);
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'LAB_HEAD')")
    @PatchMapping("/{userId}/password")
    public UserResponseDto changePassword(@PathVariable Long userId,
                                          @RequestBody String password) {
        return service.changePassword(userId, password);
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'LAB_HEAD')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto addNewUser(@Valid @RequestBody UserCreateDto createDto) {
        return service.create(createDto);
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'LAB_HEAD')")
    @PatchMapping("/{userId}")
    public UserResponseDto updateProfile(@PathVariable Long userId,
                                         @Valid @RequestBody UserUpdateDto updateDto) {
        return service.updateProfile(userId, updateDto);
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'LAB_HEAD')")
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forceDeleteById(@PathVariable Long userId) {
        service.forceDeleteById(userId);
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'LAB_HEAD')")
    @PatchMapping("/{userId}/deactivate")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void softDeleteById(@PathVariable Long userId) {
        service.softDeleteById(userId);
    }
}
