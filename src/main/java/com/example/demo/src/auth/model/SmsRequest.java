package com.example.demo.src.auth.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SmsRequest {
    private String type;
    private String contentType;
    private String countryCode;
    private String from;
    private String content;
    private List<Message> messages;
}
