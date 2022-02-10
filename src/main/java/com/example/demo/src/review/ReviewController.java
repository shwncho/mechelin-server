package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;

import com.example.demo.src.AwsS3Service;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;


import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;


@RestController
@RequestMapping("/reviews")
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
    // 메인화면 최근 리뷰
    @GetMapping("/{userIdx}")
    public BaseResponse<List<GetMainScreenReviewRes>> getMainScreenReview(@PathVariable("userIdx") int userIdx) {
        try {
            if (userIdx <= 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if (userIdx != jwtService.getUserIdx()) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetMainScreenReviewRes> getMainScreenReviewResList = reviewProvider.getMainScreenReview(userIdx);
            return new BaseResponse<>(getMainScreenReviewResList);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }



    // 상세페이지 리뷰
    @GetMapping("/{userIdx}/{storeIdx}")
    public BaseResponse<?> getDetailReview(@PathVariable("userIdx") int userIdx, @PathVariable("storeIdx") int storeIdx, @RequestParam(name = "page") int page, @RequestParam(name = "pageSize") int pageSize) {
        try {
            if(userIdx <= 0){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if (storeIdx <= 0) {
                return new BaseResponse<>(STORES_EMPTY_STORE_ID);
            }
            if (page <= 0) {
                return new BaseResponse<>(EMPTY_PAGE);
            }
            if (pageSize <= 0) {
                return new BaseResponse<>(EMPTY_PAGE_SIZE);
            }
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


    @ResponseBody
    @PatchMapping("/{userIdx}/{reviewIdx}/status")
    public BaseResponse<String> deleteReview(@PathVariable int userIdx, @PathVariable int reviewIdx){
        try {
            if(userIdx<=0){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            if(reviewIdx<=0){
                return new BaseResponse<>(REVIEWS_EMPTY_REVIEW_ID);
            }


            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdx != userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            reviewService.deleteReview(userIdx,reviewIdx);
            String result = "리뷰가 삭제되었습니다.";

            return new BaseResponse<>(result);

        } catch(BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping(value="",consumes = {"multipart/form-data"})
    public BaseResponse<PostReviewRes> createReview(@RequestPart PostReviewReq postReviewReq,
                                              @RequestPart(required = false) List<MultipartFile> imageFile){
        try{
            if(postReviewReq.getUserIdx()<=0){
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }
            int userIdxByJwt = jwtService.getUserIdx();
            if (postReviewReq.getUserIdx()!=userIdxByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(postReviewReq.getStoreIdx()<=0){
                return new BaseResponse<>(STORES_EMPTY_STORE_ID);
            }
            if(postReviewReq.getStarRate()<=0){
                return new BaseResponse<>(POST_STORE_EMPTY_STAR);
            }
            if(postReviewReq.getContents().isEmpty() && postReviewReq.getContents()==null){
                return new BaseResponse<>(POST_STORE_EMPTY_CONTENTS);
            }
            int checkNum =1;
            List<String> fileNameList = new ArrayList<>();
            for(MultipartFile image:imageFile){
                if(image.isEmpty()) checkNum=0;
            }
            if(checkNum==1) fileNameList=awsS3Service.uploadFile(imageFile);

            PostReviewRes postReviewRes = reviewService.createReview(postReviewReq, fileNameList);
            return new BaseResponse<>(postReviewRes);
        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 리뷰 수정 - 별점, 내용
    @ResponseBody
    @PatchMapping("/{userIdx}/{reviewIdx}")
    public BaseResponse<String> editReview(@PathVariable("userIdx") int userIdx, @PathVariable("reviewIdx") int reviewIdx, @RequestBody Review review) {
        try {

            if (userIdx <= 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_ID);
            }

/*            if (userIdx != jwtService.getUserIdx()) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }*/

            if (reviewIdx <= 0) {
                return new BaseResponse<>(REVIEWS_EMPTY_REVIEW_ID);
            }

            PatchReviewReq patchReviewReq = new PatchReviewReq(review.getStarRate(), review.getContents());

            if (patchReviewReq.getStarRate() <= 0) {
                return new BaseResponse<>(PATCH_REVIEW_EMPTY_STARRATE);
            }

            if (patchReviewReq.getContents() == null || patchReviewReq.getContents().equals("")) {
                return new BaseResponse<>(PATCH_REVIEW_EMPTY_CONTENTS);
            }

            reviewService.editReview(patchReviewReq, reviewIdx);

            String result = "리뷰 수정 성공했습니다.";
            return new BaseResponse<>(result);

        } catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}