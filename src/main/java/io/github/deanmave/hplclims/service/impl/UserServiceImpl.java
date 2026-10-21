package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.UserPosition;
import io.github.deanmave.hplclims.domain.UserRole;
import io.github.deanmave.hplclims.domain.dto.request.UserCreateDto;
import io.github.deanmave.hplclims.domain.dto.request.UserUpdateDto;
import io.github.deanmave.hplclims.domain.dto.response.UserResponseDto;
import io.github.deanmave.hplclims.domain.mapper.UserMapper;
import io.github.deanmave.hplclims.exception.ConflictException;
import io.github.deanmave.hplclims.exception.NotFoundException;
import io.github.deanmave.hplclims.repository.UsageLogRepository;
import io.github.deanmave.hplclims.repository.UserRepository;
import io.github.deanmave.hplclims.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final UsageLogRepository logRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponseDto create(UserCreateDto userDto) {
        log.info("Попытка добавления нового пользователя: {}", userDto);
        if (repository.existsByLogin(userDto.getLogin())) {
            throw new ConflictException("Пользователь с логином " + userDto.getLogin() + " уже существует.");
        }
        User user = mapper.toUser(userDto);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User savedUser = repository.save(user);
        log.info("Пользователь добавлен: {}", savedUser);
        return mapper.toUserResponseDto(savedUser);
    }

    @Override
    public List<UserResponseDto> getUsersFiltered(Boolean status, UserRole role, UserPosition position) {
        log.info("Запрос на получение пользователей: status={}, role={}, position={}", status, role, position);
        return repository.findFiltered(status, role, position).stream()
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
        User updatedUser = repository.save(mapper.updateFromDto(existingUser, userDto));
        log.info("Профиль пользователя обновлена: {}", updatedUser);
        return mapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public UserResponseDto changePassword(Long id, String newPassword) {
        log.info("Попытка смены пароля у пользователя с ID: {}", id);
        User existingUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
        existingUser.setPassword(passwordEncoder.encode(newPassword));
        User updatedUser = repository.save(existingUser);
        log.info("Пароль пользователя обновлен: {}", updatedUser);
        return mapper.toUserResponseDto(updatedUser);
    }

    @Override
    @Transactional
    public void forceDeleteById(Long id) {
        log.info("Попытка удаления пользователя по ID: {}", id);
        if (!repository.existsById(id)) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        if (logRepository.existsByUser_Id(id)) {
            throw new ConflictException("Нельзя удалить пользователя имеющего записи лога");
        }
        repository.deleteById(id);
        log.info("Пользователь с ID {} удален", id);
    }

    @Override
    @Transactional
    public void softDeleteById(Long id) {
        log.info("Попытка деактивации пользователя по ID: {}", id);
        if (!repository.existsById(id)) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        changeStatus(id, false);
        log.info("Пользователь с ID {} деактивирован", id);
    }
}
