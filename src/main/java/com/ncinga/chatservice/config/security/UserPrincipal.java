package com.ncinga.chatservice.config.security;

import com.ncinga.chatservice.document.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
//        List<Role> roles = this.user.getRoles();

        // @TODO: add this when authorities are added.
        /*
        for (Role role : roles) {
            List<Authority> authoritiesList = role.getAuthoritiesList();
            for (Authority authority : authoritiesList) {
                authorityList.add(new SimpleGrantedAuthority(role + "@" + authority));
            }
        }
         */
//        roles.stream().map(role -> authorityList.add(new SimpleGrantedAuthority(role.getRole())));
        return new ArrayList<>();
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
