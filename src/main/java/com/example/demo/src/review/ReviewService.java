package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.AwsS3Service;
import com.example.demo.src.review.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class ReviewService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReviewDao reviewDao;
    private final ReviewProvider reviewProvider;
    private final JwtService jwtService;
    private final AwsS3Service awsS3Service;


    @Autowired
    public ReviewService(ReviewDao reviewDao, ReviewProvider reviewProvider, JwtService jwtService, AwsS3Service awsS3Service){
        this.reviewDao = reviewDao;
        this.reviewProvider = reviewProvider;
        this.jwtService = jwtService;
        this.awsS3Service = awsS3Service;
    }

    //리뷰 삭제
    @Transactional(rollbackFor = BaseException.class)
    public void deleteReview(int reviewIdx) throws BaseException{
        try{
            List<Integer> reviewTagIdx = reviewProvider.getReviewTagIdx(reviewIdx);
            for(int idx : reviewTagIdx){
                reviewDao.deleteReviewTag(idx);
            }

            List<PatchReviewImageRes> patchReviewImageRes = reviewProvider.getReviewImageIdx(reviewIdx);
            for(PatchReviewImageRes t : patchReviewImageRes){
                int idx = t.getReviewImageIdx();
                reviewDao.deleteReviewImage(idx);
            }
//            for(PatchReviewImageRes t : patchReviewImageRes){
//                String imageUrl = t.getImageUrl();
//                awsS3Service.deleteFile(imageUrl);
//            }

            reviewDao.deleteReview(reviewIdx);


        } catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
