package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.UserRole;
import io.github.deanmave.hplclims.exception.ConflictException;
import io.github.deanmave.hplclims.exception.NotFoundException;
import io.github.deanmave.hplclims.repository.UserRepository;
import io.github.deanmave.hplclims.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public User create(User user) {
        log.info("Попытка добавления нового пользователя: {}", user);
        if (repository.existsByLogin(user.getLogin())) {
            throw new ConflictException("Пользователь с логином " + user.getLogin() + " уже существует.");
        }
        User newUser = repository.save(user);
        log.info("Пользователь добавлен: {}", newUser);
        return newUser;
    }

    @Override
    public List<User> getAll() {
        log.info("Запрос на получение всех пользователей");
        return repository.findAll();
    }

    @Override
    public List<User> getByActive(boolean status) {
        log.info("Запрос на получение пользователей по статусу активности");
        return repository.findByIsActive(status);
    }

    @Override
    public User getById(Long id) {
        log.info("Запрос поиска пользователя по ID: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID " + id + " не найден"));
    }

    @Override
    @Transactional
    public User changeStatus(Long id, boolean newStatus) {
        log.info("Попытка смены статуса пользователя с ID: {}", id);
        User existingUser = repository.findById(id)
                .orElseThrow(()-> new NotFoundException("Пользователь с ID " + id + " не найден"));
        existingUser.setActive(newStatus);
        User updatedUser = repository.save(existingUser);
        log.info("Статус пользователя с ID={} изменён на {}",id, existingUser.isActive() ? "АКТИВЕН" : "НЕАКТИВЕН");
        return updatedUser;
    }

    @Override
    @Transactional
    public User updateProfile(Long id, User newUser) {
        log.info("Попытка обновления профиля пользователя с ID: {}", id);
        User existingUser = repository.findById(id)
                .orElseThrow(()-> new NotFoundException("Пользователь с ID " + id + " не найден"));
        existingUser.setFirstName(newUser.getFirstName());
        existingUser.setLastName(newUser.getLastName());
        existingUser.setMiddleName(newUser.getMiddleName());
        User updatedUser = repository.save(existingUser);
        log.info("Профиль пользователя обновлена: {}", updatedUser);
        return updatedUser;
    }

    @Override
    @Transactional
    public User changePassword(Long id, String newPassword) {
        log.info("Попытка смены пароля у пользователя с ID: {}", id);
        User existingUser = repository.findById(id)
                .orElseThrow(()-> new NotFoundException("Пользователь с ID " + id + " не найден"));
        existingUser.setPassword(newPassword);
        User updatedUser = repository.save(existingUser);
        log.info("Пароль пользователя обновлен: {}", updatedUser);
        return updatedUser;
    }

    @Override
    @Transactional
    public User changeLogin(Long id, String newLogin) {
        log.info("Попытка смены логина у пользователя с ID: {}", id);
        User existingUser = repository.findById(id)
                .orElseThrow(()-> new NotFoundException("Пользователь с ID " + id + " не найден"));
        existingUser.setLogin(newLogin);
        User updatedUser = repository.save(existingUser);
        log.info("Логин пользователя обновлен: {}", updatedUser);
        return updatedUser;
    }

    @Override
    @Transactional
    public User changeRole(Long id, UserRole newRole) {
        log.info("Попытка смены роли у пользователя с ID: {}", id);
        User existingUser = repository.findById(id)
                .orElseThrow(()-> new NotFoundException("Пользователь с ID " + id + " не найден"));
        existingUser.setRole(newRole);
        User updatedUser = repository.save(existingUser);
        log.info("Роль пользователя обновлена: {}", updatedUser);
        return updatedUser;
    }

}
