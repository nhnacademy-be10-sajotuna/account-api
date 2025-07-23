package com.sajotuna.account.domain.dto;

import com.sajotuna.account.domain.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.sajotuna.account.domain.request.RequestUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class UserDto {
    private long id;
    private UserGradePolicyDto userGradePolicyDto;
    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private User.Status status;
    private User.AuthType authType;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime currentLoginAt;
    private User.Role role;

    public User toUser(PasswordEncoder passwordEncoder) {
        return User.ofUser(getName(), passwordEncoder.encode(getPassword()), getEmail(), getPhoneNumber(), getBirthDate());
    }

    public User toAdmin(PasswordEncoder passwordEncoder) {
        return User.ofAdmin(getName(), passwordEncoder.encode(getPassword()), getEmail(), getPhoneNumber(), getBirthDate());
    }

    public static UserDto fromRequestUserLocal(RequestUser requestUser) {
        UserDto userDto = new UserDto();
        userDto.setName(requestUser.getName());
        userDto.setPassword(requestUser.getPassword());
        userDto.setEmail(requestUser.getEmail());
        userDto.setPhoneNumber(requestUser.getPhoneNumber());
        userDto.setBirthDate(requestUser.getBirthDate());
        userDto.setAuthType(User.AuthType.LOCAL);
        userDto.setStatus(User.Status.ACTIVE);
        userDto.setCurrentLoginAt(LocalDateTime.now());
        return userDto;
    }
}
