package com.example.demo.src.store.model;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class PostStoreReq {
    private int userIdx;
    private int categoryIdx;
    private String deliveryService;
    private String storeName;
    private String address;
    private double x;
    private double y;
    private String tel;
    private List<String> tagName;
    private double starRate;
    private String contents;
}
