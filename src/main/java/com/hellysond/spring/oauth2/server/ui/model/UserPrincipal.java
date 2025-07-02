package com.hellysond.spring.oauth2.server.ui.model;

import com.hellysond.spring.oauth2.server.ui.model.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public  class UserPrincipal implements UserDetails {

    private final UserEntity user;

    public UserPrincipal(UserEntity user) {
        this.user = user;
    }

    public UserEntity getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Set<GrantedAuthority> authorities = new HashSet<>();


        authorities.add(new SimpleGrantedAuthority("admin"));


        return authorities;
    }

         @Override
         public String getPassword() {
             return user.getPassword();
         }

         @Override
         public String getUsername() {
             return user.getUsername();
         }
 }