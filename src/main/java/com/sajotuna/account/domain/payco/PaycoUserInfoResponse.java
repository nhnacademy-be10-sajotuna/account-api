package com.sajotuna.account.domain.payco;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaycoUserInfoResponse {

    private Header header;
    private Data data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Header {
        private boolean isSuccessful;
        private int resultCode;
        private String resultMessage;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private Member member;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Member {
        private String idNo;
        private String email;
        private String maskedEmail;
        private String name;
        private String genderCode;
        private String birthdayMMdd;
        private String ageGroup;
    }
}