package com.sajotuna.account.domain.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RequestUser {
    @NotNull(message = "이름은 비어 있으면 안됩니다.")
    private String name;
    @NotNull(message = "비밀번호는 비어 있으면 안됩니다.")
    private String password;
    @Email(message = "이메일 형식이 아닙니다.")
    @NotNull(message = "이메일은 비어 있으면 안됩니다.")
    private String email;
    @NotNull(message = "번호는 비어 있으면 안됩니다.")
    private String phoneNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "생일을 입력하셔야 합니다.")
    private LocalDate birthDate;
    private String address;
}
