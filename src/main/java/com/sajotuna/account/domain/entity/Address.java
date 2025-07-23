package com.sajotuna.account.domain.entity;

import com.sajotuna.account.domain.dto.AddressDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@NoArgsConstructor
@Getter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long userId;
    private String nickName;
    private String streetAddress;

    public Address(long userId, AddressDto addressDto) {
        this.userId = userId;
        this.nickName = addressDto.getNickName();
        this.streetAddress = addressDto.getStreetAddress();
    }
}
