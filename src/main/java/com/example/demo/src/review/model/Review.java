package com.example.demo.src.review.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor

public class Review {
    private int reviewIdx;      // 리뷰 index
    private float starRate;     // 별점
    private String contents;    // 리뷰 내용
}
