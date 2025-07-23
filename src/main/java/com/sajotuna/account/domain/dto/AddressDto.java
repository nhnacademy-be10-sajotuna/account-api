package com.sajotuna.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddressDto {
    private long id;
    private String nickName;
    private String streetAddress;
}
