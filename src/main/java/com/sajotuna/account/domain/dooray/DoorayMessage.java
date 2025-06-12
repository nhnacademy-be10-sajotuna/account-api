package com.sajotuna.account.domain.dooray;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoorayMessage {
    private String botName;
    private String text;
    private Attachment[] attachments;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attachment {
        private String title;
        private String text;
        private String titleLink;
        private String botIconImage;
        private String color;
    }
}
