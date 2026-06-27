package com.mutrix.prepa.application.usecases.admin;

import com.mutrix.prepa.application.dto.mappers.UserResponseMapper;
import com.mutrix.prepa.application.dto.response.UserResponse;
import com.mutrix.prepa.cors.UseCase;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@UseCase
@RequiredArgsConstructor
public class ListUsersAdminUseCase {

    private final UsersServices usersServices;

    public Page<UserResponse> execute(Integer page, Integer size) {
        return usersServices.getAllUsers(page, size)
                .map(UserResponseMapper::mapFromUser);
    }
}
