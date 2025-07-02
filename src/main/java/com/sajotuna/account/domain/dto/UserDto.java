package com.sajotuna.account.domain.dto;

import com.sajotuna.account.domain.entity.User;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.sajotuna.account.domain.request.RequestUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
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

    public static User toUser(UserDto userDto, PasswordEncoder passwordEncoder) {
        return User.ofUser(userDto.getName(), passwordEncoder.encode(userDto.getPassword()), userDto.getEmail(), userDto.getPhoneNumber(), userDto.getBirthDate());
    }

    public static User toAdmin(UserDto userDto, PasswordEncoder passwordEncoder) {
        return User.ofAdmin(userDto.getName(), passwordEncoder.encode(userDto.getPassword()), userDto.getEmail(), userDto.getPhoneNumber(), userDto.getBirthDate());
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
