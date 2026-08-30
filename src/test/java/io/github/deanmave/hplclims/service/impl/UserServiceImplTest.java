package io.github.deanmave.hplclims.service.impl;

import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.UserRole;
import io.github.deanmave.hplclims.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

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
        testUser.setId(1L);
        testUser.setLogin("testUser");
        testUser.setPassword("testPassword");
        testUser.setActive(true);
        testUser.setFirstName("Иван");
        testUser.setLastName("Иванов");
        testUser.setRole(UserRole.USER);
    }

    @Test
    void create_whenInternalCodeIsUnique_shouldSaveAndReturnColumn() {
    }

    @Test
    void getAll() {
    }

    @Test
    void getByActive() {
    }

    @Test
    void getById() {
    }

    @Test
    void changeStatus() {
    }

    @Test
    void updateProfile() {
    }

    @Test
    void changePassword() {
    }

    @Test
    void changeLogin() {
    }

    @Test
    void changeRole() {
    }
}