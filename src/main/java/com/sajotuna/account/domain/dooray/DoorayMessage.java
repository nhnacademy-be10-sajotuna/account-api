package com.sajotuna.account.domain.dooray;

import com.sajotuna.account.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class DoorayMessage {
    private String botName;
    private String text;
    private Attachment[] attachments;


    @AllArgsConstructor
    public static class Attachment {
        private String title;
        private String text;
        private String titleLink;
        private String botIconImage;
        private String color;
    }

    public DoorayMessage(String status, String email) {
        if (status.equals("inactive")) {
            this.botName = "team04.shop 봇.";
            this.text = String.format("휴면 유저 %s", email);
            this.attachments =  new DoorayMessage.Attachment[]{new DoorayMessage.Attachment(
                    "인증되었습니다. ",
                    "깨어났습니다 용사님",
                    "http://naver.com",
                    "https://static.dooray.com/static_images/dooray-bot.png",
                    "red")};
        }
    }
}
