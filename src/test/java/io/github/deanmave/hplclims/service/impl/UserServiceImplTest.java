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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
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
    @DisplayName("getAll: получение списка пользователей при их наличии в БД")
    void getAll_WhenUsersExist_ShouldReturnListOfUsers() {
        when(repository.findAll()).thenReturn(List.of(testUser));
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        assertThat(service.getAll()).containsExactly(testResponseDto);
    }

    @Test
    @DisplayName("getAll: получение пустого списка, если пользователи в БД отсутствуют")
    void getAll_WhenNoUsersExist_ShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertThat(service.getAll()).isEmpty();
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
    @DisplayName("getByActive: получение списка активных пользователей")
    void getByActive_WhenActiveUsersExist_ShouldReturnListOfUsers() {
        testUser.setActive(true);
        when(repository.findByIsActive(true)).thenReturn(List.of(testUser));
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        assertThat(service.getByActive(true)).containsExactly(testResponseDto);
    }

    @Test
    @DisplayName("getByActive: получение списка неактивных пользователей")
    void getByActive_WhenNotActiveUsersExist_ShouldReturnListOfUsers() {
        testUser.setActive(false);
        testResponseDto.setActive(false);
        when(repository.findByIsActive(false)).thenReturn(List.of(testUser));
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        assertThat(service.getByActive(false)).containsExactly(testResponseDto);
    }

    @Test
    @DisplayName("getByActive: получение пустого списка, если активные пользователи в БД отсутствуют")
    void getByActive_WhenNoUsersExist_ShouldReturnEmptyList() {
        when(repository.findByIsActive(true)).thenReturn(Collections.emptyList());

        assertThat(service.getByActive(true)).isEmpty();
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
    @DisplayName("changeLogin: обновление логина существующего пользователя")
    void changeLogin_WhenUserExists_ShouldChangeLoginAndReturnUser() {
        testResponseDto.setLogin("newLogin");

        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        when(repository.save(testUser)).thenReturn(testUser);
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        service.changeLogin(1L, "newLogin");

        assertThat(testUser.getLogin()).isEqualTo("newLogin");
    }

    @Test
    @DisplayName("changeLogin: выброс ConflictException при попытке сменить уже занятый логин")
    void changeLogin_WhenNewLoginIsTaken_ShouldThrowConflictException() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        when(repository.existsByLogin("newLogin")).thenReturn(true);

        assertThatThrownBy(() -> service.changeLogin(1L, "newLogin"))
                .isInstanceOf(ConflictException.class);

        verify(repository, never()).save(any(User.class));

        assertThat(testUser.getLogin()).isEqualTo("testLogin");
    }

    @Test
    @DisplayName("changeLogin: логин не обновляется при подаче того же логина")
    void changeLogin_WhenLoginUnchanged_ShouldNotCheckUniqueness() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        service.changeLogin(1L, testUser.getLogin());

        verify(repository, never()).existsByLogin(anyString());
    }

    @Test
    @DisplayName("changeLogin: выброс NotFoundException при попытке сменить логин несуществующего пользователя")
    void changeLogin_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeLogin(1L, "newLogin"))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("changePassword: обновление пароля существующего пользователя")
    void changePassword_WhenUserExists_ShouldChangePasswordAndReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        when(repository.save(testUser)).thenReturn(testUser);
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

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
    @DisplayName("changeRole: обновление роли существующего пользователя")
    void changeRole_WhenUserExists_ShouldChangeRoleAndReturnUser() {
        testResponseDto.setRole(UserRole.VIEWER);

        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        when(repository.save(testUser)).thenReturn(testUser);
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        service.changeRole(1L, UserRole.VIEWER);

        assertThat(testUser.getRole()).isEqualTo(UserRole.VIEWER);
    }

    @Test
    @DisplayName("changeRole: выброс NotFoundException при попытке сменить роль несуществующего пользователя")
    void changeRole_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeRole(1L, UserRole.VIEWER))
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