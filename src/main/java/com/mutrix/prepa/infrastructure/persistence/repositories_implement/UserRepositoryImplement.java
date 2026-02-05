package com.mutrix.prepa.infrastructure.persistence.repositories_implement;

import com.mutrix.prepa.domaines.models.Users;
import com.mutrix.prepa.domaines.repository_interfaces.UserRepositoryInterface;
import com.mutrix.prepa.infrastructure.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;

import java.util.Optional;

@RequiredArgsConstructor
public class UserRepositoryImplement implements UserRepositoryInterface {

    private final UserRepository userRepository;

    @Override
    public Users CreateUser(Users users) {
        return null;
    }

    @Override
    public Users GetUserById(String id) {
        return null;
    }

    @Override
    public Optional<Users> GetUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<Users> GetUserByPhone(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<Users> GetUserByFirebaseUid(String email) {
        return Optional.empty();
    }

    @Override
    public Users UpdateUser(Users users) {
        return null;
    }

    @Override
    public Page<User> GetAllUsers(int page, int size) {
        return null;
    }

    @Override
    public void DeleteUser(String id) {

    }
}
