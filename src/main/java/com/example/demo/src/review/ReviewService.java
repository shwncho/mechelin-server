package com.example.demo.src.review;

import com.example.demo.config.BaseException;
import com.example.demo.src.AwsS3Service;
import com.example.demo.src.review.model.*;
import com.example.demo.src.user.UserProvider;
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
    private final UserProvider userProvider;


    @Autowired
    public ReviewService(ReviewDao reviewDao, ReviewProvider reviewProvider, AwsS3Service awsS3Service, StoreDao storeDao, UserProvider userProvider){
        this.reviewDao = reviewDao;
        this.reviewProvider = reviewProvider;
        this.awsS3Service = awsS3Service;
        this.storeDao = storeDao;
        this.userProvider = userProvider;
    }

    //리뷰 삭제
    @Transactional(rollbackFor = BaseException.class)
    public void deleteReview(int userIdx, int reviewIdx) throws BaseException{
        try{
            if(userProvider.checkUser(userIdx)==0){
                throw new BaseException(EMPTY_USER);
            }

            if(reviewProvider.checkReview(userIdx, reviewIdx)==0){
                throw new BaseException(EMPTY_REVIEW);
            }


            List<Integer> reviewTagIdx = reviewProvider.getReviewTagIdx(userIdx,reviewIdx);
            for(int idx : reviewTagIdx){
                reviewDao.deleteReviewTag(idx);
            }

            List<Integer> reviewImageIdx = reviewProvider.getReviewImageIdx(userIdx, reviewIdx);
            for(int t : reviewImageIdx){

                reviewDao.deleteReviewImage(t);
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
    public PostReviewRes createReview(PostReviewReq postReviewReq, List<String> fileNameList) throws BaseException{
        try{
            int reviewIdx = reviewDao.createReview(postReviewReq);

            if(!(fileNameList.isEmpty()) && fileNameList!=null) {
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

            return new PostReviewRes(reviewIdx, fileNameList) ;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 리뷰 수정
    @Transactional
    public void editReview(PatchReviewReq patchReviewReq, int userIdx, int reviewIdx) throws BaseException{

        if (userProvider.checkUser(userIdx) == 0) {
            throw new BaseException(EMPTY_USER);
        }

        if (reviewProvider.checkReview(userIdx, reviewIdx) == 0) {
            throw new BaseException(EMPTY_REVIEW);
        }

        try {
            int result = reviewDao.editReview(patchReviewReq, reviewIdx);
            if (result == 0) {
                throw new BaseException(EDIT_FAIL_REVIEW);
            }
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }


}