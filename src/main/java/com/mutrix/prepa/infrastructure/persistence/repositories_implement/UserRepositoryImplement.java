package com.mutrix.prepa.infrastructure.persistence.repositories_implement;

import com.mutrix.prepa.domaines.models.Users;
import com.mutrix.prepa.domaines.repository_interfaces.UserRepositoryInterface;
import com.mutrix.prepa.infrastructure.mappers.UserEntityMapper;
import com.mutrix.prepa.infrastructure.persistence.data_repositories.UserRepository;
import com.mutrix.prepa.infrastructure.persistence.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserRepositoryImplement implements UserRepositoryInterface {

    private final UserRepository userRepository;

    @Override
    public Users CreateUser(Users users) {
        UserEntity response = userRepository.save(UserEntityMapper.toEntity(users));
        return UserEntityMapper.toDomainModel(response);
    }

    @Override
    public Users GetUserById(UUID id) {
        Optional<UserEntity> userEntityOptional = userRepository.findById(id);
        return userEntityOptional.map(UserEntityMapper::toDomainModel).orElse(null);
    }

    @Override
    public Optional<Users> GetUserByEmail(String email) {
        Optional<UserEntity> userEntityOptional = userRepository.findByEmail(email);
        return userEntityOptional.map(UserEntityMapper::toDomainModel);
    }

    @Override
    public Optional<Users> GetUserByPhone(String phone) {
        Optional<UserEntity> userEntityOptional = userRepository.findByPhone(phone);
        return userEntityOptional.map(UserEntityMapper::toDomainModel);
    }

    @Override
    public Optional<Users> GetUserByFirebaseUid(String firebaseUid) {
        Optional<UserEntity> userEntityOptional = userRepository.findByFirebaseUid(firebaseUid);
        return userEntityOptional.map(UserEntityMapper::toDomainModel);
    }

    @Override
    public Users UpdateUser(Users users) {
        UserEntity response = userRepository.save(UserEntityMapper.toEntity(users));
        return UserEntityMapper.toDomainModel(response);
    }

    @Override
    public Page<Users> GetAllUsers(Integer page, Integer size) {
        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        Page<UserEntity> userPageEntities = userRepository.findAll(pageable);
        return  userPageEntities.map(UserEntityMapper::toDomainModel);
    }

    @Override
    public void DeleteUser(String id) {
        userRepository.deleteById(UUID.fromString(id));
    }
}
