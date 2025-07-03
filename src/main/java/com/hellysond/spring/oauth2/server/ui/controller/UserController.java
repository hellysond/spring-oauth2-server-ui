package com.hellysond.spring.oauth2.server.ui.api.controller;

import com.hellysond.spring.oauth2.server.ui.api.repository.UserService;
import com.hellysond.spring.oauth2.server.ui.model.entity.UserEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/user")
    public List<UserEntity> getAllUsers(){
        return userService.getAllUsers();
    }

}
