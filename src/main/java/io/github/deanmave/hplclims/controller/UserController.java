package io.github.deanmave.hplclims.controller;

import io.github.deanmave.hplclims.domain.UserRole;
import io.github.deanmave.hplclims.domain.dto.request.UserCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.UserUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.UserResponseDto;
import io.github.deanmave.hplclims.service.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/users")
@AllArgsConstructor
public class UserController {
    private UserService service;

    @GetMapping
    public List<UserResponseDto> getUsers(@RequestParam(required = false) Boolean active) {
        if(active != null){
            return service.getByActive(active);
        }
        return service.getAll();
    }

    @GetMapping("/{userId}")
    public UserResponseDto getUserById(@PathVariable long userId) {
        return service.getById(userId);
    }

    @PatchMapping("/{userId}/{status}")
    public UserResponseDto changeStatus(@PathVariable long userId, @RequestParam boolean status) {
        return service.changeStatus(userId, status);
    }

    @PatchMapping("/{userId}/{login}")
    public UserResponseDto changeLogin(@PathVariable long userId, @RequestParam String login) {
        return service.changeLogin(userId, login);
    }

    @PatchMapping("/{userId}/{password}")
    public UserResponseDto changePassword(@PathVariable long userId, @RequestBody String password) {
        return service.changePassword(userId, password);
    }

    @PatchMapping("/{userId}/{role}")
    public UserResponseDto changeRole(@PathVariable long userId, @RequestParam UserRole role) {
        return service.changeRole(userId, role);
    }

    @PostMapping
    public UserResponseDto addNewUser(@Valid @RequestBody UserCreateDto createDto){
        return service.create(createDto);
    }

    @PatchMapping("/{userId}")
    public UserResponseDto updateProfile(@PathVariable long userId, @Valid @RequestBody UserUpdateDto updateDto){
        return service.updateProfile(userId,updateDto);
    }
}
