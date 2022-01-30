package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static com.example.demo.config.BaseResponseStatus.*;

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

    @GetMapping("/{userIdx}")
    public BaseResponse<List<GetMainScreenReviewRes>> getMainScreenReview(@PathVariable("userIdx") int userIdx) {
        try {
            if (userIdx != jwtService.getUserIdx()) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetMainScreenReviewRes> getMainScreenReviewResList = reviewProvider.getMainScreenReview(userIdx);
            return new BaseResponse<>(getMainScreenReviewResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("/{userIdx}/{storeIdx}")
    public BaseResponse<?> getDetailReview(@PathVariable("userIdx") int userIdx, @PathVariable("storeIdx") int storeIdx, @RequestParam(name = "page") int page, @RequestParam(name = "pagesize") int pageSize) {
        try {
            System.out.println(page);
            if (userIdx != jwtService.getUserIdx()) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            GetStoreInformationRes getStoreInformationRes = reviewProvider.getStoreInformation(userIdx, storeIdx);
            List<GetReviewRes> getReviewResList = reviewProvider.getReview(userIdx, storeIdx, page, pageSize);

            return new BaseResponse<>(new GetDetailReviewRes(getStoreInformationRes, getReviewResList));
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
