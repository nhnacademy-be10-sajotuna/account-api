package com.sajotuna.account.domain.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
public class RequestUser {
    @NotNull(message = "이름은 비어 있으면 안됩니다.")
    private String name;
    @NotNull(message = "비밀번호는 비어 있으면 안됩니다.")
    private String password;
    @Email
    @NotNull(message = "이메일은 비어 있으면 안됩니다.")
    private String email;
    @NotNull(message = "번호는 비어 있으면 안됩니다.")
    private String phoneNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String address;
}
