package com.sajotuna.account.domain.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RequestEditUser {
    @NotNull(message = "이름은 비어 있으면 안됩니다.")
    private String name;
    private String password;
    @NotNull(message = "번호는 비어 있으면 안됩니다.")
    private String phoneNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "생일을 입력하셔야 합니다.")
    private LocalDate birthDate;
}
