package com.example.demo.src.search.model;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor


public class GetSearchByStoreName {
    private int storeIdx;       // 가게 index
    private String storeName;   // 가게 이름
    private String address;     // 가게 주소
}
