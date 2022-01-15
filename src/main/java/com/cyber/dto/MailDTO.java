package com.cyber.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder

public class MailDTO {

    private String emailTo;
    private String emailFrom;
    private String message;
    private String token; //need this token to create email
    private String subject;
    private String url;
}
