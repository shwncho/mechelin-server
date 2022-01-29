package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.AwsS3Service;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@RequestMapping("reviews")
public class ReviewController {
    private final ReviewProvider reviewProvider;
    private final ReviewService reviewService;
    private final JwtService jwtService;


    @Autowired
    public ReviewController(ReviewProvider reviewProvider, JwtService jwtService, ReviewService reviewService) {
        this.reviewProvider = reviewProvider;
        this.jwtService = jwtService;
        this.reviewService = reviewService;
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

    @ResponseBody
    @PatchMapping("/{userIdx}/{reviewIdx}/status")
    public BaseResponse<String> deleteReview(@PathVariable int userIdx, @PathVariable int reviewIdx){
        try {
            if(userIdx==0){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if(reviewIdx==0){
                return new BaseResponse<>(REVIEWS_EMPTY_REVIEW_ID);
            }


            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            reviewService.deleteReview(reviewIdx);
            String result = "리뷰가 삭제되었습니다.";

            return new BaseResponse<>(result);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
