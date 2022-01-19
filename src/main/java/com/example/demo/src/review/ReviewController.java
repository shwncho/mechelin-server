package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.GetMainScreenReviewRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("reviews")
public class ReviewController {
    private ReviewProvider reviewProvider;
    private JwtService jwtService;

    @Autowired
    public ReviewController(ReviewProvider reviewProvider, JwtService jwtService) {
        this.reviewProvider = reviewProvider;
        this.jwtService = jwtService;
    }

    @GetMapping("/main")
    public BaseResponse<List<GetMainScreenReviewRes>> getMainScreenReview() {
        try {
            int userIdx = jwtService.getUserIdx();
            List<GetMainScreenReviewRes> getMainScreenReviewResList = reviewProvider.getMainScreenReview(userIdx);
            return new BaseResponse<>(getMainScreenReviewResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
