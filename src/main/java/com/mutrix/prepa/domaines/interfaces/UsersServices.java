package com.mutrix.prepa.domaines.interfaces;

import com.mutrix.prepa.domaines.models.UserModel;
import org.springframework.data.domain.Page;

import java.util.Optional;
import java.util.UUID;

public interface UsersServices {

    public UserModel createUser(UserModel userModel);

    public UserModel getUserById(UUID id);

    public Optional<UserModel> getUserByEmail(String email);

    public Optional<UserModel> getUserByPhone(String email);

    public Optional<UserModel> getUserByFirebaseUid(String firebaseUid);

    public UserModel updateUser(UserModel userModel, String plainPassword);

    public Page<UserModel> getAllUsers(Integer page, Integer size);

    public void deleteUser(String id);
}
