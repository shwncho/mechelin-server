package com.example.demo.src.user.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor

public class GetProfileRes {
    private String nickName;
    private String email;
    private int storeCnt;
    private int reviewCnt;

}
