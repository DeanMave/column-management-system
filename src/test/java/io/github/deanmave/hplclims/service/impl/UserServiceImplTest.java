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
import io.github.deanmave.hplclims.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис работы с пользователями")
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl service;

    private User testUser;
    private UserCreateDto testCreateDto;
    private UserUpdateDto testUpdateDto;
    private UserResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setLogin("testLogin");
        testUser.setPassword("testPassword");
        testUser.setActive(true);
        testUser.setFirstName("Иван");
        testUser.setLastName("Иванов");
        testUser.setMiddleName("Иванович");
        testUser.setRole(UserRole.USER);
        testUser.setPosition(UserPosition.CHEMIST_ANALYST);

        testCreateDto = new UserCreateDto();
        testCreateDto.setLogin("testLogin");
        testCreateDto.setPassword("password123");
        testCreateDto.setFirstName("Иван");
        testCreateDto.setLastName("Иванов");
        testCreateDto.setMiddleName("Иванович");
        testCreateDto.setRole(UserRole.USER);

        testUpdateDto = new UserUpdateDto();
        testUpdateDto.setFirstName("Олег");
        testUpdateDto.setLastName("Холмов");
        testUpdateDto.setMiddleName("Викторович");

        testResponseDto = new UserResponseDto();
        testResponseDto.setId(1L);
        testResponseDto.setLogin("testLogin");
        testResponseDto.setFirstName("Иван");
        testResponseDto.setLastName("Иванов");
        testResponseDto.setMiddleName("Иванович");
        testResponseDto.setRole(UserRole.USER);
        testResponseDto.setActive(true);
    }

    @Test
    @DisplayName("create: создание пользователя с уникальным логином")
    void create_WhenLoginIsUnique_ShouldSaveAndReturnUser() {
        when(repository.existsByLogin(testUser.getLogin())).thenReturn(false);
        when(mapper.toUser(testCreateDto)).thenReturn(testUser);
        when(repository.save(testUser)).thenReturn(testUser);
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        service.create(testCreateDto);

        verify(repository).existsByLogin(testCreateDto.getLogin());
        verify(mapper).toUser(testCreateDto);
        verify(repository).save(testUser);
        verify(mapper).toUserResponseDto(testUser);
    }

    @Test
    @DisplayName("create: выброс ConflictException при добавление пользователя с уже занятым логином")
    void create_WhenLoginAlreadyExists_ShouldThrowConflictException() {
        when(repository.existsByLogin(testCreateDto.getLogin())).thenReturn(true);

        assertThatThrownBy(() -> service.create(testCreateDto)).isInstanceOf(ConflictException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("getUsersFiltered: получение списка пользователей при их наличии в БД")
    void getAll_WhenUsersExist_ShouldReturnListOfUsers() {
        when(repository.findFiltered(null,null,null)).thenReturn(List.of(testUser));
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        assertThat(service.getUsersFiltered(null,null,null)).containsExactly(testResponseDto);
    }

    @Test
    @DisplayName("getUsersFiltered: получение пустого списка, если пользователи в БД отсутствуют")
    void getAll_WhenNoUsersExist_ShouldReturnEmptyList() {
        when(repository.findFiltered(any(),any(),any())).thenReturn(Collections.emptyList());

        assertThat(service.getUsersFiltered(any(),any(),any())).isEmpty();
    }

    @Test
    @DisplayName("getById: получение пользователя по существующему ID")
    void getById_WhenUserExist_ShouldReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        assertThat(service.getById(1L)).isEqualTo(testResponseDto);
    }

    @Test
    @DisplayName("getById: выброс NotFoundException при поиске несуществующего ID")
    void getById_WhenUserDoesNotExist_shouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("getUsersFiltered: получение списка активных пользователей")
    void getUsersFiltered_WhenActiveUsersExist_ShouldReturnListOfUsers() {
        testUser.setActive(true);
        when(repository.findFiltered(true,null,null)).thenReturn(List.of(testUser));
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        assertThat(service.getUsersFiltered(true,null,null)).containsExactly(testResponseDto);
    }

    @Test
    @DisplayName("getUsersFiltered: получение списка неактивных пользователей")
    void getUsersFiltered_WhenNotActiveUsersExist_ShouldReturnListOfUsers() {
        testUser.setActive(false);
        testResponseDto.setActive(false);
        when(repository.findFiltered(false,null,null)).thenReturn(List.of(testUser));
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        assertThat(service.getUsersFiltered(false,null,null)).containsExactly(testResponseDto);
    }

    @Test
    @DisplayName("getUsersFiltered: получение пустого списка, если пользователи в БД отсутствуют")
    void getUsersFiltered_WhenNoUsersExist_ShouldReturnEmptyList() {
        when(repository.findFiltered(null,null,null)).thenReturn(Collections.emptyList());

        assertThat(service.getUsersFiltered(null,null,null)).isEmpty();
    }

    @Test
    @DisplayName("getUsersFiltered: получение списка пользователей по должности")
    void getUsersFiltered_WhenUsersByPositionExist_ShouldReturnListOfUsers() {
        testResponseDto.setPosition(UserPosition.CHEMIST_ANALYST);
        when(repository.findFiltered(null,null,UserPosition.CHEMIST_ANALYST)).thenReturn(List.of(testUser));
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        assertThat(service.getUsersFiltered(null,null,UserPosition.CHEMIST_ANALYST)).containsExactly(testResponseDto);
    }

    @Test
    @DisplayName("changeStatus: обновление статуса существующего пользователя")
    void changeStatus_WhenUserExists_ShouldChangeStatusAndReturnUser() {
        testResponseDto.setActive(false);

        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        when(repository.save(testUser)).thenReturn(testUser);
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        service.changeStatus(1L, false);

        assertThat(testUser.isActive()).isEqualTo(false);
    }

    @Test
    @DisplayName("changeStatus: выброс NotFoundException при попытке сменить статус несуществующего пользователя")
    void changeStatus_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeStatus(1L, false))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("changePassword: обновление пароля существующего пользователя")
    void changePassword_WhenUserExists_ShouldChangePasswordAndReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        when(repository.save(testUser)).thenReturn(testUser);
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);
        when(passwordEncoder.encode(any())).thenReturn("newPassword");

        service.changePassword(1L, "newPassword");

        assertThat(testUser.getPassword()).isEqualTo("newPassword");
    }

    @Test
    @DisplayName("changePassword: выброс NotFoundException при попытке сменить пароль несуществующего пользователя")
    void changePassword_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changePassword(1L, "newPassword"))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("updateProfile: обновление профиля существующего пользователя")
    void updateProfile_WhenUserExists_ShouldUpdateProfileAndReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mapper.updateFromDto(testUser, testUpdateDto)).thenAnswer(invocation -> {
            testUser.setFirstName("Олег");
            testUser.setLastName("Холмов");
            testUser.setMiddleName("Викторович");
            testUser.setRole(UserRole.ADMIN);
            testUser.setPosition(UserPosition.SENIOR_CHEMIST_ANALYST);
            return testUser;
        });
        when(repository.save(testUser)).thenReturn(testUser);
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        service.updateProfile(1L, testUpdateDto);

        assertThat(testUser.getFirstName()).isEqualTo("Олег");
        assertThat(testUser.getLastName()).isEqualTo("Холмов");
        assertThat(testUser.getMiddleName()).isEqualTo("Викторович");
    }

    @Test
    @DisplayName("updateProfile: выброс NotFoundException при попытке обновить профиль несуществующего пользователя")
    void updateProfile_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProfile(1L, testUpdateDto))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any(User.class));
    }
}