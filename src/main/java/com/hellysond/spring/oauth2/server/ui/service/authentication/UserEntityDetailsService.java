package com.hellysond.spring.oauth2.server.ui.service.authentication;

import com.hellysond.spring.oauth2.server.ui.model.UserPrincipal;
import com.hellysond.spring.oauth2.server.ui.model.entity.UserEntity;
import com.hellysond.spring.oauth2.server.ui.repository.user.UserEntityRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserEntityDetailsService implements UserDetailsService {

    UserEntityRepository userEntityRepository;

    public UserEntityDetailsService(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userEntityRepository.findByUsername(username).orElseThrow(()->
                new UsernameNotFoundException(username));

        return new UserPrincipal(user);
    }

}
