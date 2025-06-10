package com.sajotuna.account.domain.entity;

import com.sajotuna.account.domain.dto.UserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Data
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long policyId;

    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private Status status;
    private long point;
    @Enumerated(EnumType.STRING)
    private AuthType authType;
    private LocalDateTime currentLoginAt;

    public User() {}
    public User(UserDto userDto, PasswordEncoder passwordEncoder) {
        this.email = userDto.getEmail();
        this.phoneNumber = userDto.getPhoneNumber();
        this.name = userDto.getName();
        this.password = passwordEncoder.encode(userDto.getPassword());
        this.createdAt = LocalDateTime.now();
        this.status = Status.ACTIVE;
        this.point = 5000;
        this.birthDate = userDto.getBirthDate();
        this.authType = AuthType.LOCAL;
        this.policyId = 1;
    }

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
