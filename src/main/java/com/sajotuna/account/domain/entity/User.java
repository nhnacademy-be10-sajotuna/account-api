package com.sajotuna.account.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Setter
    private String outId;

    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @Setter
    @Enumerated(EnumType.STRING)
    private Status status;
    @Setter
    @Enumerated(EnumType.STRING)
    private AuthType authType;
    @Setter
    private LocalDateTime currentLoginAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {}

    public User(String name, String password, String email, String phoneNumber, LocalDate birthDate, Role role, AuthType authType, LocalDateTime currentLoginAt, Status status) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.role = role;
        this.authType = authType;
        this.currentLoginAt = currentLoginAt;
        this.status = status;
    }

    public static User ofAdmin(String name, String password, String email, String phoneNumber, LocalDate birthDate) {
        return new User(name, password, email, phoneNumber, birthDate, Role.ADMIN, AuthType.LOCAL, LocalDateTime.now(), Status.ACTIVE);
    }

    public static User ofUser(String name, String password, String email, String phoneNumber, LocalDate birthDate) {
        return new User(name, password, email, phoneNumber, birthDate, Role.USER, AuthType.LOCAL, LocalDateTime.now(), Status.ACTIVE);
    }

    public static User ofPayco(String outId, String name, String email, AuthType authType) {
        User user = new User(name, null, email, null, null, Role.USER, authType, LocalDateTime.now(), Status.ACTIVE);
        user.setOutId(outId);
        return user;
    }

    public void setOuter(String outId, String name, String email, AuthType authType) {
        this.outId = outId;
        this.name = name;
        this.email = email;
        this.authType = authType;
    }

    public void update(String name, String phoneNumber, LocalDate birthDate) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    public void updatePassword(String password) {
        this.password = password;
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

    public enum Role{
        USER,
        ADMIN
    }


}
