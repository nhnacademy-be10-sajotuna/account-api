package com.sajotuna.account.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long policyId;

    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private LocalDateTime birthDate;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private Status status;
    private long point;
    @Enumerated(EnumType.STRING)
    private AuthType authType;
    private LocalDateTime currentLoginAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }


    public enum Status {
        ACTIVE,
        INACTIVE,
        DELETED
    }

    public enum AuthType{
        LOCAL,
        PAYCO
    }

}
