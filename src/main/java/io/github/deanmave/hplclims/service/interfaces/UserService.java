package io.github.deanmave.hplclims.service.interfaces;

import io.github.deanmave.hplclims.domain.User;
import io.github.deanmave.hplclims.domain.UserRole;

import java.util.List;

public interface UserService {
    User create(User user);

    List<User> getAll();

    User getById(Long id);

    User changeStatus(Long id, boolean newStatus);

    User updateProfile(Long id, User newUser);

    User changePassword(Long id, String newPassword);

    User changeLogin(Long id, String newLogin);

    User changeRole(Long id, UserRole newRole);
}
