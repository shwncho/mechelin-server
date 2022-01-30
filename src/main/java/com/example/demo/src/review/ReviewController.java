package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.AwsS3Service;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@RequestMapping("reviews")
public class ReviewController {
    private final ReviewProvider reviewProvider;
    private final ReviewService reviewService;
    private final JwtService jwtService;
    private final AwsS3Service awsS3Service;


    @Autowired
    public ReviewController(ReviewProvider reviewProvider, JwtService jwtService, ReviewService reviewService,AwsS3Service awsS3Service) {
        this.reviewProvider = reviewProvider;
        this.jwtService = jwtService;
        this.reviewService = reviewService;
        this.awsS3Service = awsS3Service;
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

    @ResponseBody
    @PostMapping(value="",consumes = {"multipart/form-data"})
    public BaseResponse<Integer> createReview(@RequestPart PostReviewReq postReviewReq,
                                              @RequestPart(required = false) List<MultipartFile> imageFile){
        try{
            if(postReviewReq.getUserIdx()==0){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            int userIdxByJwt = jwtService.getUserIdx();
            if (postReviewReq.getUserIdx()!=userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(postReviewReq.getStoreIdx()==0){
                return new BaseResponse<>(STORES_EMPTY_STORE_ID);
            }
            if(postReviewReq.getStarRate()==0){
                return new BaseResponse<>(POST_STORE_EMPTY_STAR);
            }
            if(postReviewReq.getContents().isEmpty()){
                return new BaseResponse<>(POST_STORE_EMPTY_CONTENTS);
            }
            List<String> fileNameList = new ArrayList<>();
            if(imageFile!=null) fileNameList = awsS3Service.uploadFile(imageFile);

            int reviewIdx = reviewService.createReview(postReviewReq, fileNameList);
            return new BaseResponse<>(reviewIdx);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
