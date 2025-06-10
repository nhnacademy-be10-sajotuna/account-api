package com.sajotuna.account.domain.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private String nickName;
    private String streetAddress;
    private String detailAddress;
}
