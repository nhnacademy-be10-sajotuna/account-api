package com.sajotuna.account.domain.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestOauth2 {
    String email;
    String name;
}
