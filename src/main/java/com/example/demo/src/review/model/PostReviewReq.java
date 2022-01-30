package com.example.demo.src.review.model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
public class PostReviewReq {
    private int userIdx;
    private int storeIdx;
    private double starRate;
    private List<String> tagName;
    private String contents;
}
