package com.example.demo.src.search.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class GetSearchRes {
    private GetSearchByHashtag hashtag;
    private List<GetSearchByStoreName> store;
}

