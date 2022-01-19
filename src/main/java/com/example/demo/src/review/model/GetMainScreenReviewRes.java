package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetMainScreenReviewRes {
    private int userIdx;
    private int storeIdx;
    private String storeName;
    private double starRate;
    private String contents;
    private String createdAt;
}
