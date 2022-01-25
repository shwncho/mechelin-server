package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetReviewRes {
    private int reviewIdx;
    private List<String> imageUrl;
    private String contents;
    private String createdAt;
}
