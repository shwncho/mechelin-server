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
import com.example.demo.src.store.StoreDao;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;


@Service
public class ReviewService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ReviewDao reviewDao;
    private final ReviewProvider reviewProvider;
    private final AwsS3Service awsS3Service;
    private final StoreDao storeDao;


    @Autowired
    public ReviewService(ReviewDao reviewDao, ReviewProvider reviewProvider, AwsS3Service awsS3Service, StoreDao storeDao){
        this.reviewDao = reviewDao;
        this.reviewProvider = reviewProvider;
        this.awsS3Service = awsS3Service;
        this.storeDao = storeDao;
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
    @Transactional(rollbackFor = BaseException.class)
    public int createReview(PostReviewReq postReviewReq, List<String> fileNameList) throws BaseException{
        try{
            int reviewIdx = reviewDao.createReview(postReviewReq);

            if(!(fileNameList.isEmpty())) {
                for (String imgURL : fileNameList) {
                    storeDao.createImage(imgURL, reviewIdx);
                }
            }
            if(!(postReviewReq.getTagName().isEmpty())) {
                for (String tag : postReviewReq.getTagName()) {
                    int tagIdx = storeDao.checkTagName(tag);

                    if (tagIdx == 0) {
                        storeDao.createTag(reviewIdx, tag);
                    } else {
                        storeDao.createIsTag(reviewIdx, tagIdx);
                    }
                }
            }

            return reviewIdx;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
