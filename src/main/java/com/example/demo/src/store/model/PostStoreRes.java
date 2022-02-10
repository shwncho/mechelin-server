package com.example.demo.src.store.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PostStoreRes {
    private int userIdx;
    private int storeIdx;
    private List<String> fileNameList;
}
