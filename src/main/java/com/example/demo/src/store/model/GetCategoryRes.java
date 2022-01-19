package com.example.demo.src.store.model;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor

public class GetCategoryRes {
    private int storeIdx;           // 가게 Index
    private String imageUrl;        // 리뷰 대표 이미지 URL
    private String storeName;       // 가게 이름
    private float starRate;         // 별점
    private List<String> tag;       // 해시태그
    private String address;         // 가게 주소
}
