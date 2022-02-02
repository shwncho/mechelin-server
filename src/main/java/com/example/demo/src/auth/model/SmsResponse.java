package com.example.demo.src.auth.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SmsResponse {
    private String requestId;
    private String requestTime;
    private String statusCode;
    private String statusName;
}
