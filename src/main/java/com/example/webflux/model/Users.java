package com.example.webflux.model;


import com.example.webflux.enums.Roles;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Document
@Setter
@Getter
public class Users implements UserDetails {


    @Id
    private String id;

    @Field(name = "name")
    private String name;

    @Indexed(unique = true)
    private String email;

    private String password;

    private String role;


    @PersistenceConstructor
    public Users(String name, String email, String password,String role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role=role;
    }

    public Users(){}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.name;
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
