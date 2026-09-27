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
    void create_WhenLoginIsUnique_ShouldSaveAndReturnUser() {
        when(repository.existsByLogin(testUser.getLogin())).thenReturn(false);
        when(mapper.toUser(testCreateDto)).thenReturn(testUser);
        when(repository.save(testUser)).thenReturn(testUser);
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        UserResponseDto result = service.create(testCreateDto);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLogin()).isEqualTo("testLogin");

        verify(repository).existsByLogin(testCreateDto.getLogin());
        verify(mapper).toUser(testCreateDto);
        verify(repository).save(testUser);
        verify(mapper).toUserResponseDto(testUser);
    }

    @Test
    void create_WhenLoginAlreadyExists_ShouldThrowConflictException() {
        when(repository.existsByLogin(testCreateDto.getLogin())).thenReturn(true);

        assertThatThrownBy(() -> service.create(testCreateDto)).isInstanceOf(ConflictException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void getAll_WhenUsersExist_ShouldReturnListOfUsers() {
        when(repository.findAll()).thenReturn(List.of(testUser));
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        assertThat(service.getAll()).containsExactly(testResponseDto);
    }

    @Test
    void getAll_WhenNoUsersExist_ShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertThat(service.getAll()).isEmpty();
    }

    @Test
    void getById_WhenUserExist_ShouldReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        assertThat(service.getById(1L)).isEqualTo(testResponseDto);
    }

    @Test
    void getById_WhenUserDoesNotExist_shouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByActive_WhenActiveUsersExist_ShouldReturnListOfUsers() {
        testUser.setActive(true);
        when(repository.findByIsActive(true)).thenReturn(List.of(testUser));
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        assertThat(service.getByActive(true)).containsExactly(testResponseDto);
    }

    @Test
    void getByActive_WhenNotActiveUsersExist_ShouldReturnListOfUsers() {
        testUser.setActive(false);
        testResponseDto.setActive(false);
        when(repository.findByIsActive(false)).thenReturn(List.of(testUser));
        when(mapper.toUserResponseDto(testUser)).thenReturn(testResponseDto);

        assertThat(service.getByActive(false)).containsExactly(testResponseDto);
    }

    @Test
    void getByActive_WhenNoUsersExist_ShouldReturnEmptyList() {
        when(repository.findByIsActive(true)).thenReturn(Collections.emptyList());

        assertThat(service.getByActive(true)).isEmpty();
    }

    @Test
    void changeStatus_WhenUserExists_ShouldChangeStatusAndReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        User savedUserDb = new User();
        savedUserDb.setId(1L);
        savedUserDb.setActive(false);
        when(repository.save(testUser)).thenReturn(savedUserDb);
        when(mapper.toUserResponseDto(savedUserDb)).thenReturn(testResponseDto);

        UserResponseDto result = service.changeStatus(1L, false);

        assertThat(result).isEqualTo(testResponseDto);
        assertThat(testUser.isActive()).isEqualTo(false);
    }

    @Test
    void changeStatus_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeStatus(1L, false))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void changeLogin_WhenUserExists_ShouldChangeLoginAndReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        User savedUserDb = new User();
        savedUserDb.setId(1L);
        savedUserDb.setLogin("newLogin");
        when(repository.save(testUser)).thenReturn(savedUserDb);
        when(mapper.toUserResponseDto(savedUserDb)).thenReturn(testResponseDto);

        UserResponseDto result = service.changeLogin(1L, "newLogin");

        assertThat(result).isEqualTo(testResponseDto);
        assertThat(testUser.getLogin()).isEqualTo("newLogin");
    }

    @Test
    void changeLogin_WhenNewLoginIsTaken_ShouldThrowConflictException() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        when(repository.existsByLogin("newLogin")).thenReturn(true);

        assertThatThrownBy(() -> service.changeLogin(1L, "newLogin"))
                .isInstanceOf(ConflictException.class);

        verify(repository, never()).save(any(User.class));

        assertThat(testUser.getLogin()).isEqualTo("testLogin");
    }

    @Test
    void changeLogin_WhenLoginUnchanged_ShouldNotCheckUniqueness() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        service.changeLogin(1L, testUser.getLogin());

        verify(repository, never()).existsByLogin(anyString());
    }

    @Test
    void changeLogin_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeLogin(1L, "newLogin"))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void changePassword_WhenUserExists_ShouldChangePasswordAndReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        User savedUserDb = new User();
        savedUserDb.setId(1L);
        savedUserDb.setPassword("newPassword");
        when(repository.save(testUser)).thenReturn(savedUserDb);
        when(mapper.toUserResponseDto(savedUserDb)).thenReturn(testResponseDto);

        UserResponseDto result = service.changePassword(1L, "newPassword");

        assertThat(result).isEqualTo(testResponseDto);
        assertThat(testUser.getPassword()).isEqualTo("newPassword");
    }

    @Test
    void changePassword_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changePassword(1L, "newPassword"))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void changeRole_WhenUserExists_ShouldChangeRoleAndReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        User savedUserDb = new User();
        savedUserDb.setId(1L);
        savedUserDb.setRole(UserRole.VIEWER);
        when(repository.save(testUser)).thenReturn(savedUserDb);
        when(mapper.toUserResponseDto(savedUserDb)).thenReturn(testResponseDto);

        UserResponseDto result = service.changeRole(1L, UserRole.VIEWER);

        assertThat(result).isEqualTo(testResponseDto);
        assertThat(testUser.getRole()).isEqualTo(UserRole.VIEWER);
    }

    @Test
    void changeRole_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.changeRole(1L, UserRole.VIEWER))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void updateProfile_WhenUserExists_ShouldUpdateProfileAndReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        User savedUserDb = new User();
        savedUserDb.setId(1L);
        savedUserDb.setFirstName("Олег");
        savedUserDb.setLastName("Холмов");
        savedUserDb.setMiddleName("Викторович");

        when(mapper.updateFromDto(testUser, testUpdateDto)).thenAnswer(invocation -> {
            testUser.setFirstName("Олег");
            testUser.setLastName("Холмов");
            testUser.setMiddleName("Викторович");
            return testUser;
        });

        testResponseDto.setFirstName("Олег");
        testResponseDto.setLastName("Холмов");
        testResponseDto.setMiddleName("Викторович");

        when(repository.save(testUser)).thenReturn(savedUserDb);
        when(mapper.toUserResponseDto(savedUserDb)).thenReturn(testResponseDto);

        UserResponseDto result = service.updateProfile(1L, testUpdateDto);

        assertThat(result).isEqualTo(testResponseDto);
        assertThat(result.getFirstName()).isEqualTo("Олег");
        assertThat(result.getLastName()).isEqualTo("Холмов");
        assertThat(result.getMiddleName()).isEqualTo("Викторович");
    }

    @Test
    void updateProfile_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProfile(1L, testUpdateDto))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any(User.class));
    }
}