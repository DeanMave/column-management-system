package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.UserRole;
import io.github.deanmave.hplclims.domain.dto.request.UserCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.UserUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.UserResponseDto;
import io.github.deanmave.hplclims.domain.mapper.UserMapper;
import io.github.deanmave.hplclims.exception.ConflictException;
import io.github.deanmave.hplclims.exception.NotFoundException;
import io.github.deanmave.hplclims.repository.UserRepository;
import io.github.deanmave.hplclims.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public UserResponseDto create(UserCreateDto userDto) {
        log.info("Попытка добавления нового пользователя: {}", userDto);
        if (repository.existsByLogin(userDto.getLogin())) {
            throw new ConflictException("Пользователь с логином " + userDto.getLogin() + " уже существует.");
        }
        User newUser = repository.save(mapper.toUser(userDto));
        log.info("Пользователь добавлен: {}", newUser);
        return mapper.toUserResponseDto(newUser);
    }

    @Override
    public List<UserResponseDto> getAll() {
        log.info("Запрос на получение всех пользователей");
        return repository.findAll().stream()
                .map(mapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDto> getByActive(boolean status) {
        log.info("Запрос на получение пользователей по статусу активности");
        return repository.findByIsActive(status).stream()
                .map(mapper::toUserResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDto getById(Long id) {
        log.info("Запрос поиска пользователя по ID: {}", id);
        return mapper.toUserResponseDto(repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден")));
    }

    @Override
    @Transactional
    public UserResponseDto changeStatus(Long id, boolean newStatus) {
        log.info("Попытка смены статуса пользователя с ID: {}", id);
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
        existingUser.setActive(newStatus);
        User updatedUser = repository.save(existingUser);
        log.info("Статус пользователя с ID={} изменён на {}", id, existingUser.isActive() ? "АКТИВЕН" : "НЕАКТИВЕН");
        return mapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public UserResponseDto updateProfile(Long id, UserUpdateDto userDto) {
        log.info("Попытка обновления профиля пользователя с ID: {}", id);
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
        User updatedUser = repository.save(mapper.updateFromDto(existingUser,userDto));
        log.info("Профиль пользователя обновлена: {}", updatedUser);
        return mapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public UserResponseDto changePassword(Long id, String newPassword) {
        log.info("Попытка смены пароля у пользователя с ID: {}", id);
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
        existingUser.setPassword(newPassword);
        User updatedUser = repository.save(existingUser);
        log.info("Пароль пользователя обновлен: {}", updatedUser);
        return mapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public UserResponseDto changeLogin(Long id, String newLogin) {
        log.info("Попытка смены логина у пользователя с ID: {}", id);
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
        if (!existingUser.getLogin().equals(newLogin)
            && repository.existsByLogin(newLogin)) {
            throw new ConflictException("login уже занят: " + newLogin);
        }
        existingUser.setLogin(newLogin);
        User updatedUser = repository.save(existingUser);
        log.info("Логин пользователя обновлен: {}", updatedUser);
        return mapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public UserResponseDto changeRole(Long id, UserRole newRole) {
        log.info("Попытка смены роли у пользователя с ID: {}", id);
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
        existingUser.setRole(newRole);
        User updatedUser = repository.save(existingUser);
        log.info("Роль пользователя обновлена: {}", updatedUser);
        return mapper.toUserResponseDto(updatedUser);
    }

}
