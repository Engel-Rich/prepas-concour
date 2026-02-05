package com.mutrix.prepa.domaines.repository_interfaces;

import com.mutrix.prepa.domaines.models.Users;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryInterface {

    public Users CreateUser(Users users);

    public Users GetUserById(String id);

    public Optional<Users> GetUserByEmail(String email);

    public Optional<Users> GetUserByPhone(String email);

    public Optional<Users> GetUserByFirebaseUid(String email);

    public Users UpdateUser(Users users);

    public Page<User> GetAllUsers(int page, int size);

    public void DeleteUser(String id);
}
