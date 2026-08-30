package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.UserRole;
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

    @InjectMocks
    private UserServiceImpl service;

    private User testUser;

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
    }

    @Test
    void create_WhenLoginIsUnique_ShouldSaveAndReturnUser() {
        when(repository.existsByLogin(testUser.getLogin())).thenReturn(false);

        User savedUserDb = new User();
        savedUserDb.setId(1L);
        savedUserDb.setLogin(testUser.getLogin());
        when(repository.save(testUser)).thenReturn(savedUserDb);

        User result = service.create(testUser);

        assertThat(result).isEqualTo(savedUserDb);
        assertThat(result.getLogin()).isEqualTo(testUser.getLogin());
        verify(repository).save(testUser);
    }

    @Test
    void create_WhenLoginAlreadyExists_ShouldThrowConflictException() {
        when(repository.existsByLogin(testUser.getLogin())).thenReturn(true);

        assertThatThrownBy(() -> service.create(testUser)).isInstanceOf(ConflictException.class);

        verify(repository, never()).save(any(User.class));
    }

    @Test
    void getAll_WhenUsersExist_ShouldReturnListOfUsers() {
        when(repository.findAll()).thenReturn(List.of(testUser));

        assertThat(service.getAll()).containsExactly(testUser);
    }

    @Test
    void getAll_WhenNoUsersExist_ShouldReturnEmptyList() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        assertThat(service.getAll()).isEmpty();
    }

    @Test
    void getById_WhenUserExist_ShouldReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThat(service.getById(1L)).isEqualTo(testUser);
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

        assertThat(service.getByActive(true)).containsExactly(testUser);
    }

    @Test
    void getByActive_WhenNotActiveUsersExist_ShouldReturnListOfUsers() {
        testUser.setActive(false);
        when(repository.findByIsActive(false)).thenReturn(List.of(testUser));

        assertThat(service.getByActive(false)).containsExactly(testUser);
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

        User result = service.changeStatus(1L, false);

        assertThat(result).isEqualTo(savedUserDb);
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

        User result = service.changeLogin(1L, "newLogin");

        assertThat(result).isEqualTo(savedUserDb);
        assertThat(testUser.getLogin()).isEqualTo("newLogin");
    }

    @Test
    void changeLogin_WhenNewLoginIsTaken_ShouldThrowConflictException() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        when(repository.existsByLogin("newLogin")).thenReturn(true);

        assertThatThrownBy(() -> service.changeLogin(1L, "newLogin"))
                .isInstanceOf(ConflictException.class);

        verify(repository,never()).save(any(User.class));

        assertThat(testUser.getLogin()).isEqualTo("testLogin");
    }

    @Test
    void changeLogin_WhenLoginUnchanged_ShouldNotCheckUniqueness() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        service.changeLogin(1L,testUser.getLogin());

        verify(repository,never()).existsByLogin(anyString());
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

        User result = service.changePassword(1L, "newPassword");

        assertThat(result).isEqualTo(savedUserDb);
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

        User result = service.changeRole(1L, UserRole.VIEWER);

        assertThat(result).isEqualTo(savedUserDb);
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

        User newUser = new User();
        newUser.setId(1L);
        newUser.setFirstName("Олег");
        newUser.setLastName("Холмов");
        newUser.setMiddleName("Викторович");

        User savedUserDb = new User();
        savedUserDb.setId(1L);

        when(repository.save(testUser)).thenReturn(savedUserDb);

        User result = service.updateProfile(1L, newUser);

        assertThat(result).isEqualTo(savedUserDb);
        assertThat(testUser.getFirstName()).isEqualTo("Олег");
        assertThat(testUser.getLastName()).isEqualTo("Холмов");
        assertThat(testUser.getMiddleName()).isEqualTo("Викторович");
    }

    @Test
    void updateProfile_WhenUserDoesNotExists_ShouldThrowNotFoundException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateProfile(1L, testUser))
                .isInstanceOf(NotFoundException.class);

        verify(repository, never()).save(any(User.class));
    }
}