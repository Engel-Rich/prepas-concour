package com.mutrix.prepa.infrastructure.implementations;

import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import com.mutrix.prepa.domaines.services.FirebaseService;
import com.mutrix.prepa.infrastructure.mappers.UserEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.UserRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UsersServiceImplement implements UsersServices {

    private final UserRepository userRepository;
    private final FirebaseService firebaseService;

    @Override
    public UserModel createUser(UserModel userModel) {
        UserEntity response = userRepository.save(UserEntityMapper.toEntity(userModel));
        return UserEntityMapper.toDomainModel(response);
    }

    @Override
    public UserModel getUserById(UUID id) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);
        return userEntityOptional.map(UserEntityMapper::toDomainModel).orElse(null);
    }

    @Override
    public Optional<UserModel> getUserByEmail(String email) {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);
        return userEntityOptional.map(UserEntityMapper::toDomainModel);
    }

    @Override
    public Optional<UserModel> getUserByPhone(String phone) {
        Optional<UserEntity> userEntityOptional = userRepository.findByPhone(phone);
        return userEntityOptional.map(UserEntityMapper::toDomainModel);
    }

    @Override
    public Optional<UserModel> getUserByFirebaseUid(String firebaseUid) {
        Optional<UserEntity> userEntityOptional = userRepository.findByFirebaseUid(firebaseUid);
        return userEntityOptional.map(UserEntityMapper::toDomainModel);
    }

    @Override
    public UserModel updateUser(UserModel userModel, String plainPassword) {
        // Vérification d'unicité Firebase + mise à jour Firebase avant toute modification locale
        if (userModel.getFirebaseUid() != null) {
            firebaseService.syncUserToFirebase(userModel, plainPassword);
        }
        UserEntity response = userRepository.save(UserEntityMapper.toEntity(userModel));
        return UserEntityMapper.toDomainModel(response);
    }

    @Override
    public Page<UserModel> getAllUsers(Integer page, Integer size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<UserEntity> userPageEntities = userRepository.findAll(pageable);
        return userPageEntities.map(UserEntityMapper::toDomainModel);
    }

    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(UUID.fromString(id));
    }
}
