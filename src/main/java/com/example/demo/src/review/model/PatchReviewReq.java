package com.example.demo.src.review.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor

public class PatchReviewReq {
    private float starRate;     // 별점
    private String contents;    // 리뷰 내용
}
