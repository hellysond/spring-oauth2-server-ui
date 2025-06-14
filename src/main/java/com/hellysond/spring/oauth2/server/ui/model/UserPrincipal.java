package com.hellysond.spring.oauth2.server.ui.model;

import com.hellysond.spring.oauth2.server.ui.model.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public  class UserPrincipal implements UserDetails {

        private final UserEntity user;

        public UserPrincipal(UserEntity user) {
            this.user = user;
        }

         @Override
         public Collection<? extends GrantedAuthority> getAuthorities() {
             return List.of();
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