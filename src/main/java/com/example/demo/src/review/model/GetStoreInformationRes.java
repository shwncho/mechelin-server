package com.example.demo.src.review.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetStoreInformationRes {
    private double starRate;
    private String storeName;
    private String address;
    private String tel;
    private List<String> tag;
}
