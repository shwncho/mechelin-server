package com.example.demo.src.search.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor


public class GetSearchByHashtag {
    private int tagIdx;         // 해시태그 index
    private String tagName;     // 해시태그 이름
    private int count;          // 해당 태그의 식당 개수
}
