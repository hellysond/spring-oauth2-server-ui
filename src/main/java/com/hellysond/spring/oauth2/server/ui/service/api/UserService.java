package com.hellysond.spring.oauth2.server.ui.api.repository;

import com.hellysond.spring.oauth2.server.ui.model.entity.UserEntity;
import com.hellysond.spring.oauth2.server.ui.repository.user.UserEntityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    UserEntityRepository userEntityRepository;

    public UserService(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }

    public List<UserEntity> getAllUsers(){
        return userEntityRepository.findAll();
    }

}
