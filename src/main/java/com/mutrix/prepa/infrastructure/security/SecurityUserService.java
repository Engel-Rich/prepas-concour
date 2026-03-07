package com.mutrix.prepa.infrastructure.security;

import com.mutrix.prepa.domaines.models.UserModel;
import com.mutrix.prepa.domaines.interfaces.UsersServices;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityUserService implements UserDetailsService {
    private final UsersServices usersServices;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final UserModel user = this.usersServices.getUserByFirebaseUid(username).
                orElseThrow(() -> new UsernameNotFoundException("User not found "));
        return new SecurityUser(user);
    }
}
