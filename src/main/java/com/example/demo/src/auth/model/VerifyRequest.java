package com.example.demo.src.auth.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyRequest {
    private String phoneNumber;
    private String certificationNumber;
}
